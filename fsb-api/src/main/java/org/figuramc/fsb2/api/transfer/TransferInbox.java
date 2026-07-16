package org.figuramc.fsb2.api.transfer;

import org.figuramc.fsb2.api.ProtocolSession;
import org.figuramc.fsb2.api.except.FSBArgumentException;
import org.figuramc.fsb2.api.except.FSBInvalidDataException;
import org.figuramc.fsb2.api.except.FSBStateException;
import org.figuramc.fsb2.api.packets.transfer.TransferClosePacketR2S;
import org.figuramc.fsb2.api.packets.transfer.TransferResendPacket;
import org.figuramc.fsb2.api.utils.Locking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.CRC32;

/**
 * State data for an inbound transfer.
 */
public final class TransferInbox {


    public enum State {
        OPEN,
        COMPLETE,
        FAILURE
    }

    private volatile String reason = null;
    private volatile State state = State.OPEN;

    public @Nullable String getReason() {
        return reason;
    }

    public @NotNull State getState() {
        return state;
    }

    public final int localTransactionID;
    public final int remoteTransactionID;
    public final int remoteID;

    public final int totalSize;
    public final int totalNumberOfChunks;
    public final long overallCRC;
    private final AtomicInteger retries = new AtomicInteger(0);

    /**
     * {@code synchronized} on each object to lock an individual chunk.
     * Acquire the {@link ReadWriteLock}'s {@code write} half to lock the entire collection
     */
    private final Object[] syncObjects;
    /**
     * Acquire the read half of this lock when <em>writing</em> data (yes, it's backwards).<br>
     * Acquire the write half of this lock when <em>reading</em> data to make sure it's not
     * modified while you're reading it.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * Chunk data. Follow instructions in {@link #syncObjects} and {@link #lock} before reading/writing.
     */
    private final byte[][] chunks;
    private final AtomicInteger actualTotalSize = new AtomicInteger(0);

    /* threadsafe required */
    private final BitSet neededChunks;

    /**
     * Create a new container for receiving transfers.
     * You should have validated the parameters of the transfer prior to constructing this object.
     *
     * @param totalSize           the total size of the transfer
     * @param totalNumberOfChunks the number of chunks the transfer is split into
     * @param overallCRC          CRC32 checksum for the entire data
     */
    public TransferInbox(
            @NotNull ProtocolSession session,
            int remoteID,
            int remoteTransactionID,
            int totalSize,
            int totalNumberOfChunks,
            long overallCRC
    ) throws FSBArgumentException {
        this.remoteTransactionID = remoteTransactionID;
        this.remoteID = remoteID;
        this.localTransactionID = session.allocateInboundTransfer(remoteID, remoteTransactionID);
        this.totalSize = totalSize;
        this.totalNumberOfChunks = totalNumberOfChunks;
        this.chunks = new byte[totalNumberOfChunks][];
        this.syncObjects = new Object[totalNumberOfChunks];
        this.overallCRC = overallCRC;
        for (int i = 0; i < totalNumberOfChunks; i++) this.syncObjects[i] = new Object();
        this.neededChunks = new BitSet(totalNumberOfChunks);
        this.neededChunks.set(0, totalNumberOfChunks);

        ProtocolSession.internal.registerIn.accept(session, this);
    }

    /**
     * Add a chunk to the container. Does nothing if the transfer is closed.
     *
     * @param chunkId   chunk ID, from 0 to ({@link #totalNumberOfChunks} - 1)
     * @param data      chunk data, preferably validated
     * @param overwrite set {@code true} to delete existing chunks. otherwise, data will be silently discarded
     *                  if data for the same chunk ID is already present
     * @throws FSBArgumentException if chunk ID is out of range or negative
     */
    public void receive(int chunkId, byte @NotNull [] data, boolean overwrite) throws FSBArgumentException {
        if (state != State.OPEN)
            return;
        if (chunkId < 0 || chunkId >= totalNumberOfChunks)
            throw new FSBArgumentException(String.format("chunk ID %d out of range; total # of chunks is %d", chunkId, totalNumberOfChunks));
        try (Locking.Resource ignored = Locking.use(lock.readLock())) {
            if (state != State.OPEN)
                return;

            synchronized (syncObjects[chunkId]) {
                synchronized (neededChunks) {
                    neededChunks.clear(chunkId);
                }
                byte[] prev = chunks[chunkId];
                if (prev != null) {
                    if (overwrite) actualTotalSize.addAndGet(-prev.length);
                    else return;
                }
                chunks[chunkId] = data;
                actualTotalSize.addAndGet(data.length);
            }
        }
    }

    /**
     * <p>
     * {@code true} if all chunks have been received. Does not validate that the data is the right length,
     * nor does it check the CRC. May return partially complete results if receive calls are in-flight.
     * </p>
     * <p>
     * <b>synchronization:</b> locks only the needed chunks bitset;
     * as such, may block some {@link #receive} calls momentarily
     * </p>
     */
    public boolean isComplete() {
        synchronized (neededChunks) {
            return neededChunks.isEmpty();
        }
    }

    /**
     * {@link #isComplete()}, plus checks that the received data is the expected length.
     * same synchronization impacts of {@link #isComplete}, but note that the length check is not locked;
     * as such this operation is technically not atomic on its own
     */
    public boolean isWhole() {
        return isComplete() && actualTotalSize.get() == totalSize;
    }

    /**
     * Reset all the data and start over.
     * You probably want to do this in case of a checksum failure.
     * Increments and returns the number of {@link #retries} (ex. 1 after this method is called for the first time).
     */
    public int reset() {
        try (Locking.Resource ignored = Locking.use(lock.writeLock())) {
            for (int i = 0; i < totalNumberOfChunks; i++) {
                chunks[i] = null;
            }
            // shouldn't be necessary if locking works, but I don't trust myself enough
            synchronized (neededChunks) {
                neededChunks.set(0, totalNumberOfChunks);
            }
            actualTotalSize.set(0);
            return retries.incrementAndGet();
        }
    }

    /**
     * Join all the chunks into a single byte array. Transfer must be complete at this point.
     *
     * @param autoClose close the transfer automatically if everything works
     * @return the joined data
     * @throws FSBStateException       if not all chunks received, or data is the wrong length
     * @throws FSBInvalidDataException if the CRC failed
     */
    public byte[] joinToByteArray(boolean autoClose) throws FSBStateException, FSBInvalidDataException {
        try (Locking.Resource igored = Locking.use(lock.writeLock())) {
            if (!isWhole()) {
                if (isComplete())
                    throw new FSBStateException(String.format(
                            "Did not receive the correct amount of data.\n  declared %dB, got %dB",
                            totalSize, actualTotalSize.get()
                    ));
                throw new FSBStateException(String.format(
                        "This transfer is not complete.\n  currently have %d/%d bytes, want %d more chunks",
                        actualTotalSize.get(), totalSize, neededChunks.cardinality()
                ));
            }
            CRC32 crc = new CRC32();
            byte[] result = new byte[totalSize];
            int c = 0;
            for (byte[] chunk : chunks) {
                int l = chunk.length;
                if (c + l > totalSize)
                    throw new RuntimeException(String.format("too much data (cur %d + len %d > expected %d)", c, l, totalSize));
                System.arraycopy(chunk, 0, result, c, l);
                crc.update(chunk, 0, l);
                c += l;
            }
            if (c != totalSize)
                throw new RuntimeException(String.format("wrong amount of data (actual %d != expected %d)", c, totalSize));
            long crcSum = crc.getValue();
            if (crcSum != overallCRC)
                throw new FSBInvalidDataException(String.format(
                        "checksum does not check out: want %08x (from sender), have %08x",
                        overallCRC,
                        crcSum
                ));

            if (autoClose) maybeComplete("Completed successfully");
            return result;
        }
    }

    /**
     * Returns an array of the remaining chunks that need to be sent to complete the transfer.
     */
    public int[] remainingChunks() {
        try (Locking.Resource ignored = Locking.use(lock.writeLock())) {
            return neededChunks.stream().toArray();
        }
    }

    public @Nullable TransferResendPacket produceResend() {
        try (Locking.Resource ignored = Locking.use(lock.writeLock())) {
            if (neededChunks.isEmpty()) return null;
            return new TransferResendPacket(remoteTransactionID, neededChunks.stream());
        }
    }

    public TransferClosePacketR2S produceClosure() throws FSBStateException {
        if (state == State.OPEN) throw new FSBStateException("not closed");
        boolean outcome = state == State.COMPLETE;
        return new TransferClosePacketR2S(remoteTransactionID, outcome);
    }

    public void maybeReject(String reason) {
        if (state == State.OPEN) {
            state = State.FAILURE;
            this.reason = reason;
        }
    }

    public void maybeComplete(String reason) {
        if (state == State.OPEN) {
            state = State.COMPLETE;
            this.reason = reason;
        }
    }
}

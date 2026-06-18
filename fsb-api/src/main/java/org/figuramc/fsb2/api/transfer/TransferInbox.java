package org.figuramc.fsb2.api.transfer;

import org.figuramc.fsb2.api.except.FSBStateException;
import org.figuramc.fsb2.api.utils.Locking;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * State data for an inbound transfer.
 */
public final class TransferInbox {
    public final int transferId;
    public final int totalSize;
    public final int totalNumberOfChunks;

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
    private final Set<Integer> neededChunks;

    public TransferInbox(int transferId, int totalSize, int totalNumberOfChunks) {
        this.transferId = transferId;
        this.totalSize = totalSize;
        this.totalNumberOfChunks = totalNumberOfChunks;
        this.chunks = new byte[totalNumberOfChunks][];
        this.syncObjects = new Object[totalNumberOfChunks];
        for (int i = 0; i < totalNumberOfChunks; i++) this.syncObjects[i] = new Object();
        this.neededChunks = IntStream.range(0, totalNumberOfChunks).boxed().collect(Collectors.toCollection(ConcurrentHashMap::newKeySet));
    }

    public void receive(int chunkId, byte[] data) {
        try (Locking.Resource ignored = Locking.use(lock.readLock())) {
            synchronized (syncObjects[chunkId]) {
                neededChunks.remove(chunkId);
                byte[] prev = chunks[chunkId];
                if (prev != null) actualTotalSize.addAndGet(-prev.length);
                chunks[chunkId] = data;
                actualTotalSize.addAndGet(data.length);
            }
        }
    }

    public boolean isComplete() {
        try (Locking.Resource ignored = Locking.use(lock.writeLock())) {
            return neededChunks.isEmpty();
        }
    }

    public boolean isCompleteAndValid() {
        return isComplete() && actualTotalSize.get() == totalSize;
    }

    public byte[] joinToByteArray() throws FSBStateException {
        try (Locking.Resource ignored = Locking.use(lock.writeLock())) {
            if (!isCompleteAndValid()) {
                if (isComplete())
                    throw new FSBStateException(String.format(
                            "Did not receive the correct amount of data.\n  declared %dB, got %dB",
                            totalSize, actualTotalSize.get()
                    ));
                throw new FSBStateException(String.format(
                        "This transfer is not complete.\n  currently have %d/%d bytes, want %d more chunks",
                        actualTotalSize.get(), totalSize, neededChunks.size()
                ));
            }
            byte[] result = new byte[totalSize];
            int c = 0;
            for (byte[] chunk : chunks) {
                int l = chunk.length;
                if (c + l > totalSize)
                    throw new RuntimeException(
                            "consistency error: ran out of space when joining the data for transfer"
                    );
                System.arraycopy(chunk, 0, result, c, l);
                c += l;
            }
            if (c != totalSize) throw new RuntimeException(
                    "consistency error: did not get expected amount of data"
            );
            return result;
        }
    }
}

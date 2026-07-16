package org.figuramc.fsb2.api.transfer;

import org.figuramc.fsb2.api.ProtocolSession;
import org.figuramc.fsb2.api.except.FSBArgumentException;
import org.figuramc.fsb2.api.except.FSBInvalidDataException;
import org.figuramc.fsb2.api.except.FSBStateException;
import org.figuramc.fsb2.api.packets.transfer.TransferChunkPacket;
import org.figuramc.fsb2.api.packets.transfer.TransferOpenPacket;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;
import java.util.zip.CRC32;

/**
 * State data for an outbound transfer.
 */
public final class TransferOutbox {
    public enum State {
        WAITING_TO_START,
        STREAMING,
        WAITING_TO_FINISH,
        SUCCESS,
        FAILURE
    }

    public State getState() {
        return state;
    }

    private State state = State.WAITING_TO_START;

    public final int localTransactionID;

    public final int totalSize;
    public final int totalNumberOfChunks;

    private final byte[][] chunks;
    private final long[] chunkCRC;
    private final long overallCRC;

    private final BitSet wantedChunks;

    public static byte[][] splitChunks(byte @NotNull [] data, int targetSizePerChunk) {
        int numberOfChunks = data.length / targetSizePerChunk;
        int numberOfFullChunks = numberOfChunks;
        // round up the number of chunks
        int extra = data.length % targetSizePerChunk;
        if (extra > 0) numberOfChunks++;

        byte[][] chunks = new byte[numberOfChunks][];
        int c = 0;
        for (int i = 0; i < numberOfFullChunks; i++, c += targetSizePerChunk) {
            byte[] chunk = new byte[targetSizePerChunk];
            System.arraycopy(data, c, chunk, 0, targetSizePerChunk);
            chunks[i] = chunk;
        }

        if (extra > 0) {
            byte[] remainder = new byte[extra];
            System.arraycopy(data, c, remainder, 0, extra);
            chunks[numberOfFullChunks] = remainder;
        }

        return chunks;
    }

    private static long crc32(byte @NotNull [] data) {
        CRC32 algo = new CRC32();
        algo.update(data, 0, data.length);
        return algo.getValue();
    }

    public TransferOutbox(
            @NotNull ProtocolSession session,
            byte @NotNull [] data,
            int targetSizePerChunk
    ) throws FSBArgumentException {
        if (targetSizePerChunk > 0xffff) throw new FSBArgumentException("Chunks cannot be more than 0xFFFF in length");
        this.localTransactionID = session.allocateOutboundTransfer();
        this.overallCRC = crc32(data);
        this.chunks = splitChunks(data, targetSizePerChunk);
        this.totalNumberOfChunks = chunks.length;
        this.chunkCRC = new long[totalNumberOfChunks];
        for (int i = 0; i < totalNumberOfChunks; i++) this.chunkCRC[i] = crc32(this.chunks[i]);
        this.totalSize = data.length;
        this.wantedChunks = new BitSet();
        this.wantedChunks.set(0, totalNumberOfChunks);

        ProtocolSession.internal.registerOut.accept(session, this);
    }

    public TransferOpenPacket produceHeader() {
        return new TransferOpenPacket(this.localTransactionID, this.totalSize, this.totalNumberOfChunks, this.overallCRC);
    }

    public TransferChunkPacket produceChunk(int index) {
        byte[] chunk = this.chunks[index];
        long crc = this.chunkCRC[index];
        try {
            return new TransferChunkPacket(this.localTransactionID, index, crc, chunk);
        } catch (FSBInvalidDataException e) {
            throw new RuntimeException(e); // Should be guarded by constructor exception
        }
    }

    public void accepted() throws FSBStateException {
        if (state == State.WAITING_TO_START) state = State.STREAMING;
        else throw new FSBStateException("Not in a state to do that");
    }
}

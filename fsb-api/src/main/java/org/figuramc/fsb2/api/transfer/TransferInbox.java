package org.figuramc.fsb2.api.transfer;

import org.figuramc.fsb2.api.except.FSBException;
import org.figuramc.fsb2.api.except.FSBStateException;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * State data for an inbound transfer.
 */
public final class TransferInbox {
    public final int transferId;
    public final int totalSize;
    public final int totalNumberOfChunks;

    private final byte[][] chunks;
    private int actualTotalSize = 0;

    private final Set<Integer> neededChunks;

    public TransferInbox(int transferId, int totalSize, int totalNumberOfChunks) {
        this.transferId = transferId;
        this.totalSize = totalSize;
        this.totalNumberOfChunks = totalNumberOfChunks;
        this.chunks = new byte[totalNumberOfChunks][];
        this.neededChunks = IntStream.range(0, totalNumberOfChunks).boxed().collect(Collectors.toSet());
    }

    public void receive(int chunkId, byte[] data) {
        neededChunks.remove(chunkId);
        byte[] prev = chunks[chunkId];
        if (prev != null) actualTotalSize -= prev.length;
        chunks[chunkId] = data;
        actualTotalSize += data.length;
    }

    public boolean isComplete() {
        return neededChunks.isEmpty();
    }

    public boolean isCompleteAndValid() {
        return isComplete() && actualTotalSize == totalSize;
    }

    public byte[] joinToByteArray() throws FSBStateException {
        if (!isCompleteAndValid()) {
            if (isComplete())
                throw new FSBStateException(String.format(
                        "Did not receive the correct amount of data.\n  declared %dB, got %dB",
                        totalSize, actualTotalSize
                ));
            throw new FSBStateException(String.format(
                    "This transfer is not complete.\n  currently have %d/%d bytes, want %d more chunks",
                    actualTotalSize, totalSize, neededChunks.size()
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
        return result;
    }
}

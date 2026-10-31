package org.figuramc.fsb2.api.transfer;

public interface TransferHandler {
    /**
     * Hook invoked before creating a transfer inbox with its parameters.
     */
    boolean beforeCreate(
            int remoteID,
            int remoteTransactionID,
            int localTransactionID,
            int totalSize,
            int totalNumberOfChunks,
            long overallCRC
    );

    /**
     * Hook invoked after creating a transfer inbox.
     */
    void afterCreate(
            TransferInbox inbox
    );

    boolean beforeChunk(
            TransferInbox inbox
    );

    void afterChunk(
            TransferInbox inbox
    );

    void afterComplete(
            TransferInbox inbox
    );
}

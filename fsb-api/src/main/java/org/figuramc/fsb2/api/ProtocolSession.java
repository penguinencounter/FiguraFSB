package org.figuramc.fsb2.api;

import org.figuramc.fsb2.api.except.FSBArgumentException;
import org.figuramc.fsb2.api.utils.FSBLogger;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * a session! either direction works. you might want to extend this for your client and server implementations.
 */
public class ProtocolSession {
    public final FSBLogger logger;
    public final boolean isClient;

    private final AtomicInteger nextOutboundTransferId = new AtomicInteger(0);
    private final AtomicInteger nextInboundTransferId = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> inboundTransferMap
            = new ConcurrentHashMap<>();

    public ProtocolSession(FSBLogger logger, boolean isClient) {
        this.logger = logger;
        this.isClient = isClient;
    }

    /**
     * Get a new transfer ID. This ID is unique to the session's outbound transfers.
     * This can never fail.
     */
    public int allocateOutboundTransfer() {
        return nextOutboundTransferId.incrementAndGet();
    }

    /**
     * Get a new transfer ID unique to the session's inbound transfers.
     * Only one transfer ID can be assigned per client + remote ID combination.
     *
     * @param clientId         unique ID for the sending end of this transfer; set to 0 to represent the client
     *                         when on the logical client
     * @param remoteTransferId the transfer ID provided by the sending side.
     * @return a unique inbound transfer ID to represent this transfer
     * @throws FSBArgumentException if the client + remote ID combination is already allocated
     */
    public int allocateInboundTransfer(int clientId, int remoteTransferId) throws FSBArgumentException {
        ConcurrentHashMap<Integer, Integer> clientMap = inboundTransferMap.computeIfAbsent(clientId, ConcurrentHashMap::new);
        int nextId = nextInboundTransferId.incrementAndGet();
        if (clientMap.putIfAbsent(remoteTransferId, nextId) != null)
            throw new FSBArgumentException(String.format("Already allocated inbound transfer {client=%d, remoteTx=%d}", clientId, remoteTransferId));
        return nextId;
    }

    /**
     * Get the existing transfer ID (registered with {@link #allocateInboundTransfer}) for
     * a client + remote transfer ID combination.
     *
     * @param clientId         unique ID for the sending end of this transfer; set to 0 to represent the client
     *                         when on the logical client
     * @param remoteTransferId transfer ID provided by the sending side
     * @return the allocated local ID for this inbound transfer, or {@code null} if none found
     */
    public @Nullable Integer lookupInboundTransfer(int clientId, int remoteTransferId) {
        ConcurrentHashMap<Integer, Integer> clientMap = inboundTransferMap.get(clientId);
        if (clientMap == null) return null;
        return clientMap.get(remoteTransferId);
    }
}

package org.figuramc.fsb2.api;

import com.google.common.collect.MapMaker;
import org.figuramc.fsb2.api.except.FSBArgumentException;
import org.figuramc.fsb2.api.transfer.TransferInbox;
import org.figuramc.fsb2.api.transfer.TransferOutbox;
import org.figuramc.fsb2.api.utils.FSBLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * a session! either direction works. you might want to extend this for your client and server implementations.
 */
public class ProtocolSession {
    /**
     * Weak map relating opaque connection objects (probably your Minecraft's Connection objects, or anything else
     * that can be used to identify which session is correct)
     * <p>
     * Note that in the case of singleplayer, the physical client is acting as both the client and the server.
     * This means that <b>two sessions are happening at the same time</b>! It is <i>not safe</i> to assume that
     * there is only one session.
     */
    private static final Map<Object, ProtocolSession> relations = new MapMaker().weakKeys().concurrencyLevel(8).makeMap();

    public final FSBLogger logger;
    public final boolean isClient;

    private final AtomicInteger nextOutboundTransferId = new AtomicInteger(0);
    private final AtomicInteger nextInboundTransferId = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> inboundTransferMap
            = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, TransferInbox> inboundTransfers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, TransferOutbox> outboundTransfers = new ConcurrentHashMap<>();

    private final Map<Object, Integer> remotes = new MapMaker().weakKeys().concurrencyLevel(8).makeMap();

    public ProtocolSession(FSBLogger logger, boolean isClient) {
        this.logger = logger;
        this.isClient = isClient;
    }

    /**
     * Get a session from a relevant context object.
     *
     * @param context anything relevant that has been previously {@link #relate}d
     * @return the session
     * @throws FSBArgumentException if there is no relation
     */
    public static @NotNull ProtocolSession assertLookup(Object context) throws FSBArgumentException {
        ProtocolSession session = relations.get(context);
        if (session == null) throw new FSBArgumentException(String.format("No session for context object %s", context));
        return session;
    }

    /**
     * Get a session from a relevant context object.
     *
     * @param context anything relevant that has been previously {@link #relate}d
     * @return the session or null if nothing found
     */
    public static @Nullable ProtocolSession lookup(Object context) {
        return relations.get(context);
    }

    /**
     * Create a weak relation between the context object and this session.
     *
     * @param context anything relevant
     * @throws FSBArgumentException if the anything is already associated with a different session
     */
    public void relate(Object context, int remoteID) throws FSBArgumentException {
        ProtocolSession resultOfInsert = relations.putIfAbsent(context, this);
        if (resultOfInsert != null && resultOfInsert != this)
            throw new FSBArgumentException(String.format("This context object is already related to a different context: %s", resultOfInsert));
        Integer resultOfInsert2 = remotes.putIfAbsent(context, remoteID);
        if (resultOfInsert2 != null && resultOfInsert2 != remoteID)
            throw new FSBArgumentException(String.format("This context object is already related to a different remote ID: %s", resultOfInsert2));
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
     * @param remoteID         unique ID for the sending end of this transfer; set to 0 to represent the server
     *                         when on the logical client
     * @param remoteTransferId the transfer ID provided by the sending side.
     * @return a unique inbound transfer ID to represent this transfer
     * @throws FSBArgumentException if the client + remote ID combination is already allocated
     */
    public int allocateInboundTransfer(int remoteID, int remoteTransferId) throws FSBArgumentException {
        if (isClient && remoteID != 0)
            throw new FSBArgumentException("Client side should only ever have zero as its remote id");

        ConcurrentHashMap<Integer, Integer> clientMap = inboundTransferMap.computeIfAbsent(remoteID, ConcurrentHashMap::new);
        int nextId = nextInboundTransferId.incrementAndGet();
        if (clientMap.putIfAbsent(remoteTransferId, nextId) != null)
            throw new FSBArgumentException(String.format("Already allocated inbound transfer {remote=%d, remoteTx=%d}", remoteID, remoteTransferId));
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

    public int getRemoteID(Object context) throws FSBArgumentException {
        Integer result = remotes.get(context);
        if (result == null)
            throw new FSBArgumentException(String.format("No remote ID available for context %s", context));
        return result;
    }

    public @Nullable TransferInbox inboxFor(int clientID, int transactionID) {
        return inboundTransfers.get(lookupInboundTransfer(clientID, transactionID));
    }

    public @Nullable TransferOutbox outboxFor(int transactionID) {
        return outboundTransfers.get(transactionID);
    }

    private void register(TransferOutbox obj) {
        this.outboundTransfers.put(obj.localTransactionID, obj);
    }

    private void register(TransferInbox obj) {
        this.inboundTransfers.put(obj.localTransactionID, obj);
    }

    /**
     * All the implementation details that are not part of the public API but need access beyond the current package
     */
    public static class internal {
        public static final BiConsumer<ProtocolSession, TransferOutbox> registerOut = ProtocolSession::register;
        public static final BiConsumer<ProtocolSession, TransferInbox> registerIn = ProtocolSession::register;
    }
}

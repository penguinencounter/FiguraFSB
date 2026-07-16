package org.figuramc.fsb2.api;

import com.google.common.collect.MapMaker;
import org.figuramc.fsb2.api.except.FSBArgumentException;
import org.figuramc.fsb2.api.except.FSBStateException;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.PacketHandler;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.api.transfer.TransferInbox;
import org.figuramc.fsb2.api.transfer.TransferOutbox;
import org.figuramc.fsb2.api.utils.FSBLogger;
import org.figuramc.fsb2.api.utils.Locking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
    private static final ConcurrentMap<Object, ProtocolSession> relations = new MapMaker().weakKeys().concurrencyLevel(8).makeMap();

    public final @NotNull FSBLogger logger;
    public final boolean isClient;

    /**
     * Reference to the client or server object.
     * <ul>
     * <li>On the server side, this is the MinecraftServer instance.</li>
     * <li>On the client side, this is the Minecraft instance. ({@code == Minecraft.getInstance()})</li>
     * </ul>
     */
    public final WeakReference<Object> bindRef;

    private final AtomicInteger nextOutboundTransferId = new AtomicInteger(0);
    private final AtomicInteger nextInboundTransferId = new AtomicInteger(0);
    /**
     * map: {@code remoteID -> { theirs -> ours }}
     */
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> inboundTransferMap
            = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer, TransferInbox> inboundTransfers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, TransferOutbox> outboundTransfers = new ConcurrentHashMap<>();

    // Fields related to remotes and maintaining them
    private final AtomicInteger nextRemoteId = new AtomicInteger(0);
    private final ConcurrentMap<Object, PlayerSession> remotes = new MapMaker().weakKeys().concurrencyLevel(8).makeMap();
    /**
     * Map between phantom references used in the {@link #remoteDisposal} and their corresponding session objects.
     */
    private final HashMap<PhantomReference<Object>, PlayerSession> remoteTracking = new HashMap<>();
    private final HashMap<PlayerSession, PhantomReference<Object>> remoteTrackingInv = new HashMap<>();
    /**
     * Lock protecting {@link #remotes}, {@link #remoteTracking}, and {@link #remoteTrackingInv}.
     */
    private final ReadWriteLock remoteLock = new ReentrantReadWriteLock();

    /**
     * This refqueue is used to keep track of improperly disposed remotes. Since the {@code context}
     * can be deleted at any time (the maps have weak keys, after all), {@link #delRemote} is technically
     * not required. This queue handles cases that don't use {@link #delRemote}.
     */
    private final ReferenceQueue<Object> remoteDisposal = new ReferenceQueue<>();

    private final ConcurrentHashMap<Packets.PacketRecord<?>, PacketHandler<?>> handlers = new ConcurrentHashMap<>();

    public ProtocolSession(@NotNull FSBLogger logger, Object bindRef, boolean isClient) {
        this.logger = logger;
        this.bindRef = new WeakReference<>(bindRef);
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
    public void relate(Object context, PlayerSession session) throws FSBArgumentException {
        ProtocolSession resultOfInsert = relations.putIfAbsent(context, this);
        if (resultOfInsert != null && resultOfInsert != this)
            throw new FSBArgumentException(String.format("This context object is already related to a different context: %s", resultOfInsert));
        try (Locking.Resource ignored = Locking.use(remoteLock.writeLock())) {
            PlayerSession resultOfInsert2 = remotes.putIfAbsent(context, session);
            if (resultOfInsert2 != null && !resultOfInsert2.equals(session))
                throw new FSBArgumentException(String.format("This context object is already related to a different player session: %s", resultOfInsert2));
            if (resultOfInsert2 != null) {
                // Create a new reference for this context object.
                PhantomReference<Object> theRef = new PhantomReference<>(context, remoteDisposal);
                remoteTracking.put(theRef, session);
                remoteTrackingInv.put(session, theRef);
            }
        }
    }

    private final AtomicBoolean isDisposingAlready = new AtomicBoolean(false);

    private void disposeAutomatically() {
        if (isDisposingAlready.compareAndSet(false, true)) {
            Reference<?> r = remoteDisposal.poll();
            if (r == null) return;
            try (Locking.Resource ignored = Locking.use(remoteLock.writeLock())) {
                do {
                    //noinspection SuspiciousMethodCalls
                    PlayerSession session = remoteTracking.remove(r);
                    if (session == null) continue;
                    remoteTrackingInv.remove(session);
                    remotes.values().remove(session);
                    cleanUpSession(session);
                } while ((r = remoteDisposal.poll()) != null);
            }
            isDisposingAlready.set(false);
        }
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

    public PlayerSession getRemote(Object context) throws FSBArgumentException {
        PlayerSession result;
        try (Locking.Resource ignored = Locking.use(remoteLock.readLock())) {
            result = remotes.get(context);
        }
        if (result == null)
            throw new FSBArgumentException(String.format("No session available for context %s", context));
        return result;
    }

    public int newRemote(Object context, PlayerInfo player) throws FSBArgumentException {
        int id = nextRemoteId.incrementAndGet();
        relate(context, new PlayerSession(player, id));
        return id;
    }

    public void delRemote(Object context) {
        try (Locking.Resource ignored = Locking.use(remoteLock.writeLock())) {
            PlayerSession ref = this.remotes.remove(context);
            // Already disposed
            if (ref == null) return;
            Reference<?> k = remoteTrackingInv.remove(ref);
            // If <k> is null then we can't continue anyway
            if (k == null) return;
        }
        disposeAutomatically();
    }

    /**
     * Session is deleted, manually or automatically.
     */
    private void cleanUpSession(PlayerSession session) {
        ConcurrentHashMap<Integer, Integer> toDispose = inboundTransferMap.remove(session.sessionID);
        if (toDispose == null) return;
        for (Integer ours : toDispose.values()) {
            TransferInbox box = inboundTransfers.remove(ours);
            box.maybeReject("session destroyed");
        }
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

    public <T extends Packet<?>> void onReceive(Packets.PacketRecord<T> record, PacketHandler<T> handler) throws FSBStateException {
        if (this.handlers.putIfAbsent(record, handler) != null)
            throw new FSBStateException("Handler is already registered");
    }

    public <T extends Packet<?>> void handlePacket(T packet, Object context) {
        Packets.PacketRecord<?> record = packet.identify();
        //noinspection unchecked :(
        PacketHandler<T> handler = (PacketHandler<T>) this.handlers.get(record);
        if (handler == null) return;
        handler.handle(packet, context);
    }

    /**
     * All the implementation details that are not part of the public API but need access beyond the current package
     */
    public static class internal {
        public static final BiConsumer<ProtocolSession, TransferOutbox> registerOut = ProtocolSession::register;
        public static final BiConsumer<ProtocolSession, TransferInbox> registerIn = ProtocolSession::register;
    }
}

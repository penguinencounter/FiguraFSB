package org.figuramc.fsb2.server;

import org.figuramc.fsb2.api.packets.transfer.TransferOpenPacket;

import java.util.concurrent.atomic.AtomicReference;

import static org.figuramc.fsb2.server.FSBServerLifecycleEvents.INSTANCE;

public class BuiltInEventHandlers {
    static void init() {
        INSTANCE.ON_SERVER_CREATED.register(BuiltInEventHandlers::onServerCreated, 0);
    }

    private static void onServerCreated(FSBServerLifecycleEvents.ServerCreated e, Void ignored) {
        ServerSession session = e.session;
        FSBServerEvents bus = session.eventBus;

        bus.BEFORE_ANY_TRANSFER.register(BuiltInEventHandlers::defaultBeforeTransfer, 0);

        // This object will be retained due to closures being handed to `session`.
        new BuiltInPacketHandlers(session).setup();
    }

    private static void defaultBeforeTransfer(
            FSBServerEvents.SimplePacketWrapEvent<TransferOpenPacket> param,
            ServerSession sess,
            AtomicReference<Boolean> out
    ) {
        // quick validation to make sure that things aren't going to break
        boolean result = true;
        int bytesPerChunk = param.packet.totalSize / param.packet.numberOfChunks;
        if (param.packet.totalSize < 0) result = false;
        if (bytesPerChunk > 0xffff) result = false;

        // the code will not compile without this
        final boolean result2 = result;

        // Coerce null, or an existing 'true' to 'false' if checks fail
        out.updateAndGet(it -> it == null ? result2 : it && result2);
    }
}

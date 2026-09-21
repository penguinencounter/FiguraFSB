package org.figuramc.fsb2.server;

import static org.figuramc.fsb2.server.FSBServerLifecycleEvents.INSTANCE;

public class BuiltInEventHandlers {
    static void init() {
        INSTANCE.ON_SERVER_CREATED.register(BuiltInEventHandlers::onServerCreated, 0);
    }

    private static void onServerCreated(FSBServerLifecycleEvents.ServerCreated e, Void ignored) {
        ServerSession session = e.session;
        // This object will be retained due to closures being handed to `session`.
        new BuiltInPacketHandlers(session).setup();
    }
}

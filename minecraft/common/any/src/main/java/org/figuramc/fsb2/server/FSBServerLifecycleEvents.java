package org.figuramc.fsb2.server;

import org.figuramc.fsb2.api.utils.EventSystem;

/**
 * Event handlers that last for the lifetime of the entire game (and are not tied to any specific server thread).
 * Does not have any specific execution semantics.
 */
public class FSBServerLifecycleEvents extends EventSystem {
    @Override
    protected void enqueue(Runnable action) {
        action.run();
    }

    private FSBServerLifecycleEvents() {
    }

    public static final FSBServerLifecycleEvents INSTANCE = new FSBServerLifecycleEvents();

    public static class ServerCreated extends Event {
        public final ServerSession session;

        public ServerCreated(ServerSession session) {
            this.session = session;
        }
    }

    public final EventBus<ServerCreated, Void> ON_SERVER_CREATED = new EventBus<>();
}

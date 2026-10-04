package org.figuramc.fsb2.server;

import org.figuramc.fsb2.api.utils.ImmediateEventSystem;

/**
 * Event handlers that last for the lifetime of the entire game (and are not tied to any specific server thread).
 * Does not have any specific execution semantics.
 */
public class FSBServerLifecycleEvents {
    private FSBServerLifecycleEvents() {
    }

    public static class ServerCreated extends ImmediateEventSystem.Event {
        public final ServerSession session;

        public ServerCreated(ServerSession session) {
            this.session = session;
        }
    }

    public static final ImmediateEventSystem.EventBus<ServerCreated, Void> ON_SERVER_CREATED = new ImmediateEventSystem.EventBus<>();
}

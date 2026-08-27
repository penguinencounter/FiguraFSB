package org.figuramc.fsb2.server;

import org.figuramc.fsb2.api.ProtocolSession;
import org.figuramc.fsb2.api.config.ServerIdentification;
import org.figuramc.fsb2.api.packets.s2c.S2CHelloPacket;
import org.figuramc.fsb2.server.internals.NetworkingService;

public class ServerSession extends ProtocolSession {
    public final ServerScheduler scheduler = new ServerScheduler();

    /**
     * Default server extension.
     */
    public ServerSession(Object server) {
        super(FSB.LOGGER, server, false);
        prepareServerSessionLogic();
    }

    private void prepareServerSessionLogic() {
        // Occasionally broadcast hellos
        scheduler.enqueue(new ServerScheduler.IntervalTicker(() -> {
            Object server = bindRef.get();
            if (server == null) return; // server is dead anyway
            // TODO: Actually store and load configuration
            NetworkingService.SERVICE.broadcast(server, new S2CHelloPacket(ServerIdentification.defaultValues()));
        }, 100));
    }
}

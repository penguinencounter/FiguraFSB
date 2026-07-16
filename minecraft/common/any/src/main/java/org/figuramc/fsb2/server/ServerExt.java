package org.figuramc.fsb2.server;

import org.figuramc.fsb2.api.ProtocolSession;

public class ServerExt {
    public final ProtocolSession session;

    /**
     * Default server extension.
     */
    public ServerExt(Object server) {
        session = new ProtocolSession(FSB.LOGGER, server, false);
    }

}

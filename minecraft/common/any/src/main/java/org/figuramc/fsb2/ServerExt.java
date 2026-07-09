package org.figuramc.fsb2;

import org.figuramc.fsb2.api.ProtocolSession;

public class ServerExt {
    public final ProtocolSession session;

    /**
     * Default server extension.
     */
    public ServerExt() {
        session = new ProtocolSession(FSB.LOGGER, false);
    }

}

package org.figuramc.fsb2.server;

import net.minecraft.server.MinecraftServer;
import org.figuramc.fsb2.api.ProtocolSession;

public class FSBVersionSpecific {
    public static void startServer(MinecraftServer minecraftServer) {
        ServerExt attachments = FSB.serverInit(minecraftServer);
        ProtocolSession session = attachments.session;
        BuiltInHandlers.setupTransferHandling(session);
    }
}

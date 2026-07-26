package org.figuramc.fsb2.server.versioned;

import net.minecraft.server.MinecraftServer;
import org.figuramc.fsb2.api.ProtocolSession;
import org.figuramc.fsb2.server.BuiltInHandlers;
import org.figuramc.fsb2.server.FSB;
import org.figuramc.fsb2.server.ServerExt;

public class FSBVersionSpecific {
    public static void startServer(MinecraftServer minecraftServer) {
        ServerExt attachments = FSB.serverInit(minecraftServer);
        ProtocolSession session = attachments.session;
        BuiltInHandlers.setupTransferHandling(session);
    }
}

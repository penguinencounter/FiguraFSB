package org.figuramc.fsb2.server;

import net.minecraft.server.MinecraftServer;
import org.figuramc.fsb2.FSB;
import org.figuramc.fsb2.ServerExt;

public class FSBVersionSpecific {
    public static void startServer(MinecraftServer minecraftServer) {
        ServerExt attachments = FSB.serverInit(minecraftServer);
    }
}

package org.figuramc.fsb2.server.versioned;

import net.minecraft.server.MinecraftServer;
import org.figuramc.fsb2.server.FSB;

public class FSBVersionSpecific {
    public static void startServer(MinecraftServer minecraftServer) {
        FSB.serverInit(minecraftServer);
    }
}

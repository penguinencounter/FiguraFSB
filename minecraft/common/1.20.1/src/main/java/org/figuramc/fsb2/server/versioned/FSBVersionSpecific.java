package org.figuramc.fsb2.server.versioned;

import net.minecraft.server.MinecraftServer;
import org.figuramc.fsb2.server.FSB;
import org.figuramc.fsb2.server.ServerSession;

public class FSBVersionSpecific {
    public static void startServer(MinecraftServer minecraftServer) {
        FSB.serverInit(minecraftServer);
    }

    public static void tick(MinecraftServer minecraftServer) {
        ServerSession container = FSB.servers.get(minecraftServer);
        if (container != null) container.scheduler.tick();
        else FSB.LOGGER.warn("ticking before server init'd for FSB");
    }
}

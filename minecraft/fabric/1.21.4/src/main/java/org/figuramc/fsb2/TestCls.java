package org.figuramc.fsb2;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import org.figuramc.fsb2.services.FSBInitializerService;

public class TestCls implements FSBInitializerService {
    @Override
    public void init() {
        FSB.LOGGER.info("Fabric 1.21.4!");

        ServerLifecycleEvents.SERVER_STARTING.register(TestCls::serverInit);
    }

    private static void serverInit(MinecraftServer server) {
        final ServerExt att = FSB.serverInit(server);
    }

    @Override
    public int priority() {
        return 5;
    }
}

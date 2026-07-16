package org.figuramc.fsb2.server;

import org.figuramc.fsb2.server.internals.InitializerService;
import org.figuramc.fsb2.server.internals.logging.LogService;
import org.figuramc.fsb2.server.internals.logging.LoggingProxy;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.WeakHashMap;

public class FSB {
    public static final LoggingProxy LOGGER = LogService.getLogger();

    public static final WeakHashMap<Object, ServerExt> servers = new WeakHashMap<>();

    public static void init() {
        LOGGER.info("FSB server environment (common/any)");
        InitializerService.runInitializers();
    }

    public static @NotNull ServerExt serverInit(Object /* MinecraftServer */ minecraftServer) {
        // Create the new protocol and such
        ServerExt att = new ServerExt(minecraftServer);
        servers.put(minecraftServer, att);
        return att;
    }

    public static void serverDelete(Object minecraftServer) {
        servers.remove(minecraftServer);
    }

    public static @NotNull ServerExt serverGet(Object minecraftServer) {
        return Objects.requireNonNull(servers.get(minecraftServer));
    }
}

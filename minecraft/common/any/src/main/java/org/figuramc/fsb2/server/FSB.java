package org.figuramc.fsb2.server;

import org.figuramc.fsb2.api.utils.LoggingProxy;
import org.figuramc.fsb2.server.internals.InitializerService;
import org.figuramc.fsb2.server.internals.logging.LogService;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.WeakHashMap;

public class FSB {
    public static final LoggingProxy LOGGER = LogService.getLogger();

    public static final WeakHashMap<Object, ServerSession> servers = new WeakHashMap<>();

    public static FSBEnvType environment;

    public static void init(FSBEnvType side) {
        FSB.environment = side;
        LOGGER.info(
                "Hello from FSB! This is the SERVER (any/any) component. It appears that this is a Minecraft {}. See you on the other side...",
                side
        );
        BuiltInEventHandlers.init();
        InitializerService.runInitializers();
    }

    public static @NotNull ServerSession serverInit(Object /* MinecraftServer */ minecraftServer) {
        // Create the new protocol and such
        ServerSession att = new ServerSession(minecraftServer);
        servers.put(minecraftServer, att);
        // Announce the new server to all plugins
        FSBServerLifecycleEvents.INSTANCE.ON_SERVER_CREATED.dispatch(
                new FSBServerLifecycleEvents.ServerCreated(att),
                null
        );
        return att;
    }

    public static void serverDelete(Object minecraftServer) {
        servers.remove(minecraftServer);
    }

    public static @NotNull ServerSession serverGet(Object minecraftServer) {
        return Objects.requireNonNull(servers.get(minecraftServer));
    }
}

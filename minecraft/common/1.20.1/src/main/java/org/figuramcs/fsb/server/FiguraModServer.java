package org.figuramcs.fsb.server;

import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.figuramc.fsb.api.FiguraServer;
import org.figuramc.fsb2.service_impl.FSBLogging;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.UUID;

public abstract class FiguraModServer extends FiguraServer {
    public static final String MOD_ID = "fsb";
    public static final Logger LOGGER = FSBLogging.LOGGER;
    private MinecraftServer server;

    @Override
    public Path getFiguraFolder() {
        return Path.of("fsb");
    }

    @Override
    public void logInfo(String text) {
        LOGGER.info(text);
    }

    @Override
    public void logError(String text) {
        LOGGER.error(text);
    }

    @Override
    public void logError(String text, Throwable err) {
        LOGGER.error(text, err);
    }

    @Override
    public void logDebug(String text) {
        LOGGER.debug(text);
    }

    public static FiguraModServer getInstance() {
        return (FiguraModServer) INSTANCE;
    }

    @Override
    public void sendMessage(UUID receiver, JsonObject component) {
        ServerPlayer player = getServer().getPlayerList().getPlayer(receiver);
        if (player != null) player.sendSystemMessage(Component.Serializer.fromJson(component));
    }

    protected MinecraftServer getServer() {
        return server;
    }

    @Override
    public void close() {
        server = null;
        super.close();
    }

    public final void finishInitialization(MinecraftServer server) {
        if (this.server != null) throw new IllegalStateException("Server already initialized");
        this.server = server;
    }
}

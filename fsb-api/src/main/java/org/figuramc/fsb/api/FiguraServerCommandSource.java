package org.figuramc.fsb.api;

import com.google.gson.JsonObject;

import java.util.UUID;

public interface FiguraServerCommandSource {
    default FiguraServer getServer() {
        return FiguraServer.getInstance();
    }
    UUID getExecutorUUID();
    default FiguraUser getExecutor() {
        UUID uuid = getExecutorUUID();
        return uuid != null ? getServer().userManager().getUser(uuid) : null;
    }
    default boolean permission(String permission) {
        return getServer().getPermission(getExecutorUUID(), FiguraPermissionNodes.fromString(permission));
    }
    default void sendComponent(JsonObject message) {
        getServer().sendMessage(getExecutorUUID(), message);
    }
}

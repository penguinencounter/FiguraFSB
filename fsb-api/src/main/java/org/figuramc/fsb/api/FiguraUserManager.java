package org.figuramc.fsb.api;

import org.figuramc.fsb.api.events.Events;
import org.figuramc.fsb.api.events.users.LoadPlayerDataEvent;
import org.figuramc.fsb.api.events.users.SavePlayerDataEvent;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class FiguraUserManager {
    private final FiguraServer parent;
    private final HashMap<UUID, FiguraUser> users = new HashMap<>();
    private int pingsTickCounter = 0;

    public FiguraUserManager(FiguraServer parent) {
        this.parent = parent;
    }

    public FiguraUser getUserOrNull(UUID playerUUID) {
        return users.get(playerUUID);
    }

    public boolean userExists(UUID player) {
        return users.containsKey(player) ||
                parent.getUserdataFile(player).toFile().exists() ||
                parent.getOldUserdataFile(player).toFile().exists();
    }

    public FiguraUser getUser(UUID player) {
        return users.computeIfAbsent(player, (p) -> loadPlayerData(player));
    }

    public FiguraUser setupOnlinePlayer(UUID uuid) {
        FiguraUser user = getUser(uuid);
        user.setOnline();
        user.update();
        return user;
    }


    private FiguraUser loadPlayerData(UUID player) {
        LoadPlayerDataEvent playerDataEvent = Events.call(new LoadPlayerDataEvent(player));
        if (playerDataEvent.returned()) return playerDataEvent.returnValue();
        try {
            Path dataFile = parent.getUserdataFile(player);
            return FiguraUser.load(player, dataFile);
        }
        catch (Exception e) {
            Path dataFile = parent.getOldUserdataFile(player);
            return FiguraUser.loadByteBuf(player, dataFile);
        }
    }

    public void forEachUser(Consumer<FiguraUser> func) {
        users.forEach((id, user) -> {
            if (user.online()) {
                func.accept(user);
            }
        });
    }

    public void onUserLeave(UUID player) {
        users.computeIfPresent(player, (uuid, pl) -> {
            if (!Events.call(new SavePlayerDataEvent(pl)).isCancelled())
                pl.save(parent.getUserdataFile(pl.uuid()));
            pl.setOffline();
            return pl;
        });
    }

    public void close() {
        for (FiguraUser user: users.values()) {
            if (!Events.call(new SavePlayerDataEvent(user)).isCancelled())
                user.save(parent.getUserdataFile(user.uuid()));
        }
        users.clear();
    }

    public void tick() {
        if (pingsTickCounter == 20) {
            forEachUser(user -> user.pingCounter().reset());
            pingsTickCounter = 0;
        }
        pingsTickCounter++;
    }

    private static final class FutureHandle {
        private final UUID user;
        private final CompletableFuture<FiguraUser> future;

        private FutureHandle(UUID user, CompletableFuture<FiguraUser> future) {
            this.user = user;
            this.future = future;
        }

        public UUID user() {
            return user;
        }

        public CompletableFuture<FiguraUser> future() {
            return future;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            FutureHandle that = (FutureHandle) obj;
            return Objects.equals(this.user, that.user) &&
                    Objects.equals(this.future, that.future);
        }

        @Override
        public int hashCode() {
            return Objects.hash(user, future);
        }

        @Override
        public String toString() {
            return "FutureHandle[" +
                    "user=" + user + ", " +
                    "future=" + future + ']';
        }
    }
}

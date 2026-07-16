package org.figuramc.fsb2.api;

import java.util.Objects;
import java.util.UUID;

/**
 * A player.
 */
public class PlayerInfo {
    public final UUID uuid;
    public String displayName;

    public PlayerInfo(UUID uuid, String displayName) {
        this.uuid = uuid;
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerInfo that = (PlayerInfo) o;
        return Objects.equals(uuid, that.uuid) && Objects.equals(displayName, that.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, displayName);
    }

    @Override
    public String toString() {
        return String.format("PlayerInfo{uuid=%s, displayName='%s'}", uuid, displayName);
    }
}

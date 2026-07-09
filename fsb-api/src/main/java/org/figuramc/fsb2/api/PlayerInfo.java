package org.figuramc.fsb2.api;

import java.lang.ref.WeakReference;
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
}

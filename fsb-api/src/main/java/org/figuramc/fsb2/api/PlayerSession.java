package org.figuramc.fsb2.api;

import java.util.Objects;
import java.util.UUID;

/**
 * A player that is connected to FSB in some way.
 */
public class PlayerSession {
    /**
     * "the server" when on the logical client. uuid is all zeroes, and session id is zero
     */
    public static final PlayerSession SERVER = new PlayerSession(new PlayerInfo(new UUID(0, 0), "the server"), 0);

    public final PlayerInfo info;
    public final int sessionID;

    public PlayerSession(PlayerInfo info, int sessionID) {
        this.info = info;
        this.sessionID = sessionID;
    }

    public PlayerSession(UUID uuid, String displayName, int sessionID) {
        this.info = new PlayerInfo(uuid, displayName);
        this.sessionID = sessionID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSession that = (PlayerSession) o;
        return sessionID == that.sessionID && Objects.equals(info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, sessionID);
    }

    @Override
    public String toString() {
        return String.format("PlayerSession{info=%s, sessionID=%d}", info, sessionID);
    }
}

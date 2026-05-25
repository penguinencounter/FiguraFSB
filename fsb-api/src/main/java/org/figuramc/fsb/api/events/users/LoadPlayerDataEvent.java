package org.figuramc.fsb.api.events.users;

import org.figuramc.fsb.api.FiguraUser;
import org.figuramc.fsb.api.events.ReturnableEvent;

import java.util.UUID;

public class LoadPlayerDataEvent extends ReturnableEvent<FiguraUser> {
    private final UUID player;

    public LoadPlayerDataEvent(UUID player) {
        this.player = player;
    }

    public UUID player() {
        return player;
    }
}

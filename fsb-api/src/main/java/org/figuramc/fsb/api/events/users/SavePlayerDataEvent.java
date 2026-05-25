package org.figuramc.fsb.api.events.users;

import org.figuramc.fsb.api.FiguraUser;
import org.figuramc.fsb.api.events.CancellableEvent;

public class SavePlayerDataEvent extends CancellableEvent {
    private final FiguraUser user;

    public SavePlayerDataEvent(FiguraUser user) {
        this.user = user;
    }

    public FiguraUser user() {
        return user;
    }
}

package org.figuramc.fsb.api.events.avatars;

import org.figuramc.fsb.api.events.ReturnableEvent;
import org.figuramc.fsb.api.utils.Hash;

public class AvatarExistenceFetchEvent extends ReturnableEvent<Boolean> {
    private final Hash hash;

    public AvatarExistenceFetchEvent(Hash hash) {
        this.hash = hash;
    }

    public Hash hash() {
        return hash;
    }
}

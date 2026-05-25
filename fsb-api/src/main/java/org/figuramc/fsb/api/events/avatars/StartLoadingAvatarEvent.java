package org.figuramc.fsb.api.events.avatars;

import org.figuramc.fsb.api.events.ReturnableEvent;
import org.figuramc.fsb.api.utils.Hash;

public class StartLoadingAvatarEvent extends ReturnableEvent<byte[]> {
    private final Hash hash;
    public StartLoadingAvatarEvent(Hash hash) {
        this.hash = hash;
    }

    public Hash hash() {
        return hash;
    }
}

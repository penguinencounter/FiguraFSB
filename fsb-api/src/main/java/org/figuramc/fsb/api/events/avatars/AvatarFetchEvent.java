package org.figuramc.fsb.api.events.avatars;

import org.figuramc.fsb.api.events.CancellableEvent;
import org.figuramc.fsb.api.utils.Hash;

public class AvatarFetchEvent extends CancellableEvent {
    private final Hash hash;

    public AvatarFetchEvent(Hash hash) {
        this.hash = hash;
    }
}

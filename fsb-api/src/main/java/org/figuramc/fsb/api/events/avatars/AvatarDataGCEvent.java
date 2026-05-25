package org.figuramc.fsb.api.events.avatars;

import org.figuramc.fsb.api.events.CancellableEvent;

public class AvatarDataGCEvent extends CancellableEvent {
    private final byte[] hash;

    public AvatarDataGCEvent(byte[] hash) {
        this.hash = hash;
    }

    public byte[] hash() {
        return hash;
    }
}

package org.figuramc.fsb.api.events.avatars;

import org.figuramc.fsb.api.events.CancellableEvent;
import org.figuramc.fsb.api.utils.Hash;

public class RemoveAvatarDataEvent extends CancellableEvent {
    private final Hash avatarHash;

    public RemoveAvatarDataEvent(Hash avatarHash) {
        this.avatarHash = avatarHash;
    }

    public Hash avatarHash() {
        return avatarHash;
    }
}

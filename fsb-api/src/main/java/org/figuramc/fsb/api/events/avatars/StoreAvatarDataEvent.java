package org.figuramc.fsb.api.events.avatars;

import org.figuramc.fsb.api.events.CancellableEvent;
import org.figuramc.fsb.api.utils.Hash;

public class StoreAvatarDataEvent extends CancellableEvent {
    private final byte[] avatarData;
    private final Hash avatarHash;

    public StoreAvatarDataEvent(byte[] avatarData, Hash avatarHash) {
        this.avatarData = avatarData;
        this.avatarHash = avatarHash;
    }

    public byte[] avatarData() {
        return avatarData;
    }

    public Hash avatarHash() {
        return avatarHash;
    }
}

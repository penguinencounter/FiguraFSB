package org.figuramc.fsb.api.events.avatars;

import org.figuramc.fsb.api.avatars.FiguraServerAvatarManager;
import org.figuramc.fsb.api.events.CancellableEvent;
import org.figuramc.fsb.api.utils.Hash;

public class StoreAvatarMetadataEvent extends CancellableEvent {
    private final Hash hash;
    private final FiguraServerAvatarManager.AvatarMetadata metadata;

    public StoreAvatarMetadataEvent(Hash hash, FiguraServerAvatarManager.AvatarMetadata metadata) {
        this.hash = hash;
        this.metadata = metadata;
    }

    public Hash hash() {
        return hash;
    }

    public FiguraServerAvatarManager.AvatarMetadata metadata() {
        return metadata;
    }
}

package org.figuramc.fsb.api.events.avatars;

import org.figuramc.fsb.api.avatars.FiguraServerAvatarManager;
import org.figuramc.fsb.api.events.ReturnableEvent;
import org.figuramc.fsb.api.utils.Hash;

public class StartLoadingMetadataEvent extends ReturnableEvent<FiguraServerAvatarManager.AvatarMetadata> {
    private final Hash hash;

    public StartLoadingMetadataEvent(Hash hash) {
        this.hash = hash;
    }

    public Hash hash() {
        return hash;
    }
}

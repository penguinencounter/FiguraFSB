package org.figuramc.fsb.api.events.avatars;

import org.figuramc.fsb.api.events.Event;
import org.figuramc.fsb.api.utils.Hash;

public class AvatarDeletionException extends Event {
    private final Hash hash;
    private final Exception exception;

    public AvatarDeletionException(Hash hash, Exception e) {
        this.hash = hash;
        exception = e;
    }

    public Hash hash() {
        return hash;
    }

    public Exception exception() {
        return exception;
    }
}

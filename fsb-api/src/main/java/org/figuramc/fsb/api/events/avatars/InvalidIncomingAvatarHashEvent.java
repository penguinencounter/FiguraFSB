package org.figuramc.fsb.api.events.avatars;

import org.figuramc.fsb.api.events.Event;
import org.figuramc.fsb.api.utils.Hash;

public class InvalidIncomingAvatarHashEvent extends Event {
    private final Hash expectedHash;
    private final Hash receivedHash;

    public InvalidIncomingAvatarHashEvent(Hash expectedHash, Hash receivedHash) {
        this.expectedHash = expectedHash;
        this.receivedHash = receivedHash;
    }

    public Hash expectedHash() {
        return expectedHash;
    }

    public Hash receivedHash() {
        return receivedHash;
    }
}

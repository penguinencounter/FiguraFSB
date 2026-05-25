package org.figuramc.fsb.api.events.avatars;

import org.figuramc.fsb.api.events.Event;
import org.figuramc.fsb.api.utils.Hash;

import java.util.List;
import java.util.UUID;

public class InvalidAvatarHashEvent extends Event {
    private final Hash expectedHash;
    private final Hash receivedHash;
    private final List<UUID> awaitingUsers;

    public InvalidAvatarHashEvent(Hash expectedHash, Hash receivedHash, List<UUID> awaitingUsers) {
        this.expectedHash = expectedHash;
        this.receivedHash = receivedHash;
        this.awaitingUsers = awaitingUsers;
    }

    public Hash expectedHash() {
        return expectedHash;
    }

    public Hash receivedHash() {
        return receivedHash;
    }

    public List<UUID> awaitingUsers() {
        return awaitingUsers;
    }
}

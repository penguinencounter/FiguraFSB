package org.figuramc.fsb.api.exceptions;

import org.figuramc.fsb.api.utils.Hash;

public class HashNotMatchingException extends RuntimeException {
    private final Hash expectedHash;
    private final Hash actualHash;

    public HashNotMatchingException(Hash expectedHash, Hash actualHash) {
        this.expectedHash = expectedHash;
        this.actualHash = actualHash;
    }

    @Override
    public String toString() {
        return String.format(
                "Expected hash %s for avatar data, got %s",
                expectedHash, actualHash
        );
    }

    public Hash expectedHash() {
        return expectedHash;
    }

    public Hash actualHash() {
        return actualHash;
    }
}

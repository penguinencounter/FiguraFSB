package org.figuramc.fsb.api.avatars;

import org.figuramc.fsb.api.utils.Hash;

import java.util.Objects;

public final class EHashPair {
    private final Hash hash;
    private final Hash ehash;

    public EHashPair(Hash hash, Hash ehash) {
        this.hash = hash;
        this.ehash = ehash;
    }

    public Hash hash() {
        return hash;
    }

    public Hash ehash() {
        return ehash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        EHashPair that = (EHashPair) obj;
        return Objects.equals(this.hash, that.hash) &&
                Objects.equals(this.ehash, that.ehash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, ehash);
    }

    @Override
    public String toString() {
        return "EHashPair[" +
                "hash=" + hash + ", " +
                "ehash=" + ehash + ']';
    }

}

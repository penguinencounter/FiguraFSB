package org.figuramc.fsb2.api.utils;

import java.util.Arrays;

public class Hash {
    private final byte[] hash;
    private final char[] HEX = "0123456789abcdef".toCharArray();


    public Hash(byte[] hash) {
        if (hash.length != 32) throw new IllegalArgumentException("Invalid hash length");
        this.hash = hash;
    }

    public byte[] get() {
        return hash.clone();
    }

    @Override
    public String toString() {
        char[] hexChars = new char[hash.length * 2];
        for (int i = 0; i < hash.length; i++) {
            int widened = hash[i] & 0xff;
            hexChars[i * 2] = HEX[widened >>> 4];
            hexChars[i * 2 + 1] = HEX[widened & 0xf];
        }
        return new String(hexChars);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hash)) return false;
        Hash hash1 = (Hash) o;
        return Arrays.equals(hash, hash1.hash);
    }

    public static Hash empty() {
        return new Hash(new byte[32]);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }
}

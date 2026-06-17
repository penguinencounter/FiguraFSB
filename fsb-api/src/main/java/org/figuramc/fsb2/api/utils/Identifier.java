package org.figuramc.fsb2.api.utils;

import java.util.Objects;

/**
 * Identifier. Also known as a resource location.
 */
public final class Identifier {
    private final String namespace;
    private final String path;

    /**
     * @param namespace Namespace of identifier
     * @param path      Path of identifier
     */
    public Identifier(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public static Identifier figura(String path) {
        return new Identifier("figura", path);
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }

    public static Identifier parse(String ident) {
        int i = ident.indexOf(':');
        if (i == -1) return new Identifier("minecraft", ident);
        return new Identifier(ident.substring(0, i), ident.substring(i + 1));
    }

    public String namespace() {
        return namespace;
    }

    public String path() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Identifier that = (Identifier) obj;
        return Objects.equals(this.namespace, that.namespace) &&
                Objects.equals(this.path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }

}
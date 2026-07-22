package org.figuramc.fsb2.server;

public enum FSBEnvType {
    /**
     * Client builds have both the client and potentially multiple integrated servers.
     */
    CLIENT,
    /**
     * Server builds have no client.
     */
    SERVER
}

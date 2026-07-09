package org.figuramc.fsb2;

import org.figuramc.fsb2.api.packets.Packet;

@FunctionalInterface
public interface ServerPacketHandler<C, T extends Packet<?>> {
    void handle(C connection, T packet);
}

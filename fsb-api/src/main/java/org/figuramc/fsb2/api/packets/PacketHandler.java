package org.figuramc.fsb2.api.packets;

@FunctionalInterface
public interface PacketHandler<P extends Packet<?>> {
    void handle(P packet, Object context);
}

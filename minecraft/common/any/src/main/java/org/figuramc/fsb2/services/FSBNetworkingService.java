package org.figuramc.fsb2.services;

import org.figuramc.fsb2.ServerPacketHandler;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;

public interface FSBNetworkingService<C> {
    void send(C connection, Packet<?> packet);

    <T extends Packet<?>> void onReceive(Packets.PacketRecord<T> packetType, ServerPacketHandler<C, T> handler);
}

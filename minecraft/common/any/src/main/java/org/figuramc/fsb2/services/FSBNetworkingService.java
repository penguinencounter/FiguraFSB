package org.figuramc.fsb2.services;

import org.figuramc.fsb2.api.packets.Packet;

public interface FSBNetworkingService<C> {
    void send(C connection, Packet<?> packet);
}

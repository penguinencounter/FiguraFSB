package org.figuramc.fsb.api.packets.handlers.c2s;

import org.figuramc.fsb.api.packets.Packet;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;

import java.util.UUID;

public interface C2SPacketHandler<P extends Packet> {
    P serialize(IFriendlyByteBuf byteBuf);
    void handle(UUID sender, P packet);
}

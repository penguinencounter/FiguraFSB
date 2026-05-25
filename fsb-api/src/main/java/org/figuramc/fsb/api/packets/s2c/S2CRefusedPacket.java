package org.figuramc.fsb.api.packets.s2c;

import org.figuramc.fsb.api.packets.Packet;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;
import org.figuramc.fsb.api.utils.Identifier;

public class S2CRefusedPacket implements Packet {
    public static final Identifier PACKET_ID = new Identifier("figura", "refused");

    @Override
    public void write(IFriendlyByteBuf buf) {

    }

    @Override
    public Identifier getId() {
        return PACKET_ID;
    }
}

package org.figuramc.fsb2.api.packets.s2c;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

public class S2CAdvertisePacket implements Packet<S2CAdvertisePacket> {
    public static final Packets.PacketRecord<S2CAdvertisePacket> REC = rec(
            Identifier.fsb("s2c/advertise"),
            S2CAdvertisePacket::new
    );

    public S2CAdvertisePacket(IFriendlyByteBuf buf, Object context) {

    }

    @Override
    public void write(IFriendlyByteBuf buf) {

    }

    @Override
    public Packets.PacketRecord<S2CAdvertisePacket> identify() {
        return REC;
    }
}

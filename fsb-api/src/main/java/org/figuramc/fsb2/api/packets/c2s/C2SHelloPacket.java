package org.figuramc.fsb2.api.packets.c2s;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

public class C2SHelloPacket implements Packet<C2SHelloPacket> {
    public static final Packets.PacketRecord<C2SHelloPacket> REC = rec(
            Identifier.fsb("c2s/hello"),
            C2SHelloPacket::new
    );

    public C2SHelloPacket(IFriendlyByteBuf buf, Object context) {
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
    }

    @Override
    public Packets.PacketRecord<C2SHelloPacket> identify() {
        return REC;
    }
}

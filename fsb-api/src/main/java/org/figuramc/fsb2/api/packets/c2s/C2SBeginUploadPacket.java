package org.figuramc.fsb2.api.packets.c2s;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

public class C2SBeginUploadPacket implements Packet<C2SBeginUploadPacket> {
    public static final Packets.PacketRecord<C2SBeginUploadPacket> REC = rec(
            Identifier.fsb("c2s/upload_begin"),
            C2SBeginUploadPacket::new
    );

    public C2SBeginUploadPacket() {
        throw new RuntimeException("TODO");
    }

    public C2SBeginUploadPacket(IFriendlyByteBuf buf, Object context) {
        throw new RuntimeException("TODO");
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        throw new RuntimeException("TODO");
    }

    @Override
    public Packets.PacketRecord<C2SBeginUploadPacket> identify() {
        return REC;
    }
}

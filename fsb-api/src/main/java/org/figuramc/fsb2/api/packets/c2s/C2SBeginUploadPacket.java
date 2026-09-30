package org.figuramc.fsb2.api.packets.c2s;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.api.utils.Identifier;

import java.nio.charset.StandardCharsets;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

public final class C2SBeginUploadPacket implements Packet<C2SBeginUploadPacket> {
    public static final Packets.PacketRecord<C2SBeginUploadPacket> REC = rec(
            Identifier.fsb("c2s/upload_begin"),
            C2SBeginUploadPacket::new
    );

    public static final int MAX_SLOT_LENGTH = 128;

    public final int transferId;
    public final String avatarSlot;

    public C2SBeginUploadPacket(int transferId, String avatarSlot) {
        this.transferId = transferId;
        this.avatarSlot = avatarSlot;
    }

    public C2SBeginUploadPacket(IFriendlyByteBuf buf, Object context) {
        this.transferId = buf.readInt();
        this.avatarSlot = new String(buf.readByteArray(MAX_SLOT_LENGTH), StandardCharsets.UTF_8);
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(transferId);
        buf.writeByteArray(
                avatarSlot.substring(0, Math.min(avatarSlot.length(), MAX_SLOT_LENGTH - 1))
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public Packets.PacketRecord<C2SBeginUploadPacket> identify() {
        return REC;
    }
}

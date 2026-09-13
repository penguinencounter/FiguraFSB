package org.figuramc.fsb2.api.packets.s2c;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.api.utils.Identifier;

import java.nio.charset.StandardCharsets;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

public class S2CUploadResponsePacket implements Packet<S2CUploadResponsePacket> {
    public static final Packets.PacketRecord<S2CUploadResponsePacket> REC = rec(
            Identifier.fsb("s2c/upload_response"),
            S2CUploadResponsePacket::new
    );

    public final boolean shouldContinue;
    public final String message;

    public S2CUploadResponsePacket(boolean shouldContinue, String message) {
        this.shouldContinue = shouldContinue;
        this.message = message;
    }

    public S2CUploadResponsePacket(IFriendlyByteBuf buf, Object context) {
        this.shouldContinue = buf.readByte() > 0;
        this.message = new String(buf.readByteArray(256), StandardCharsets.UTF_8);
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeByte(shouldContinue ? 1 : 0);
        buf.writeByteArray(message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Packets.PacketRecord<S2CUploadResponsePacket> identify() {
        return REC;
    }
}

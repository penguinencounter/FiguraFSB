package org.figuramc.fsb2.api.packets.s2c;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.api.utils.Identifier;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

public final class S2CUploadResponsePacket implements Packet<S2CUploadResponsePacket> {
    public static final Packets.PacketRecord<S2CUploadResponsePacket> REC = rec(
            Identifier.fsb("s2c/upload_response"),
            S2CUploadResponsePacket::new
    );

    public enum Status {
        // Do not re-use identifiers. Used: 1, 2, 3
        REJECT(1),
        CONTINUE(2),
        COMPLETE(3);

        public final byte ident;

        Status(int ident) {
            this.ident = (byte) ident;
        }
    }

    public static final HashMap<Byte, Status> statusDecode;

    @NotNull public final Status status;
    @NotNull public final String message;

    public S2CUploadResponsePacket(@NotNull Status status, @NotNull String message) {
        this.status = status;
        this.message = message;
    }

    public S2CUploadResponsePacket(IFriendlyByteBuf buf, Object context) {
        this.status = statusDecode.get(buf.readByte());
        this.message = new String(buf.readByteArray(256), StandardCharsets.UTF_8);
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeByte(status.ident);
        buf.writeByteArray(message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Packets.PacketRecord<S2CUploadResponsePacket> identify() {
        return REC;
    }

    static {
        statusDecode = new HashMap<>();
        for (Status value : Status.values()) {
            statusDecode.put(value.ident, value);
        }
    }
}

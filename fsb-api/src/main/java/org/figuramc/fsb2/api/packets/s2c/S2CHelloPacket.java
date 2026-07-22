package org.figuramc.fsb2.api.packets.s2c;

import org.figuramc.fsb2.api.config.ServerIdentification;
import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

public class S2CHelloPacket implements Packet<S2CHelloPacket> {
    public static final Packets.PacketRecord<S2CHelloPacket> REC = rec(
            Identifier.fsb("s2c/hello"),
            S2CHelloPacket::new
    );

    public final ServerIdentification serverId;

    public S2CHelloPacket(ServerIdentification serverId) {
        this.serverId = serverId;
    }

    public S2CHelloPacket(IFriendlyByteBuf buf, Object context) {
        this.serverId = ServerIdentification.decode(buf);
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        serverId.write(buf);
    }

    @Override
    public Packets.PacketRecord<S2CHelloPacket> identify() {
        return REC;
    }
}

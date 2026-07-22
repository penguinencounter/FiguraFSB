package org.figuramc.fsb2.api.packets.s2c;

import org.figuramc.fsb2.api.config.ServerIdentification;
import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/**
 * Something's changed!
 * <p>
 * Also used to communicate per-player permissions changes.
 */
public class S2CReconfigurePacket implements Packet<S2CReconfigurePacket> {
    public static final Packets.PacketRecord<S2CReconfigurePacket> REC = rec(
            Identifier.fsb("s2c/reconfigure"),
            S2CReconfigurePacket::new
    );

    public final ServerIdentification serverId;

    public S2CReconfigurePacket(ServerIdentification serverId) {
        this.serverId = serverId;
    }

    public S2CReconfigurePacket(IFriendlyByteBuf buf, Object context) {
        this.serverId = ServerIdentification.decode(buf);
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        serverId.write(buf);
    }

    @Override
    public Packets.PacketRecord<S2CReconfigurePacket> identify() {
        return REC;
    }
}

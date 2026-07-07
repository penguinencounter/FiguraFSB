package org.figuramc.fsb2.api.packets.transfer;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets.PacketRecord;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/**
 * receiver -> sender.
 * signal to sender to begin sending chunks.
 */
public final class AcceptTransferPacket implements Packet<AcceptTransferPacket> {
    public static final PacketRecord<AcceptTransferPacket> REC = rec(
            Identifier.figura("transfer/accept"),
            AcceptTransferPacket::new
    );

    public final int transactionID;

    public AcceptTransferPacket(int transactionID) {
        this.transactionID = transactionID;
    }

    public AcceptTransferPacket(IFriendlyByteBuf buf, Object context) {
        this.transactionID = buf.readInt();
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(transactionID);
    }

    @Override
    public PacketRecord<AcceptTransferPacket> identify() {
        return REC;
    }
}

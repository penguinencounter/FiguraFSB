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
public final class TransferAcceptPacket implements Packet<TransferAcceptPacket> {
    public static final PacketRecord<TransferAcceptPacket> REC = rec(
            Identifier.fsb("transfer/accept"),
            TransferAcceptPacket::new
    );

    public final int transactionID;

    public TransferAcceptPacket(int transactionID) {
        this.transactionID = transactionID;
    }

    public TransferAcceptPacket(IFriendlyByteBuf buf, Object context) {
        this.transactionID = buf.readInt();
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(transactionID);
    }

    @Override
    public PacketRecord<TransferAcceptPacket> identify() {
        return REC;
    }
}

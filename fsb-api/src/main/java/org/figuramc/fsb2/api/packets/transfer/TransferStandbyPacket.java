package org.figuramc.fsb2.api.packets.transfer;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets.PacketRecord;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/**
 * sender -> receiver.
 * the sender is done sending chunks for now; the receiver can either ask for
 * some chunks to be sent again or finish up the transfer.
 */
public final class TransferStandbyPacket implements Packet<TransferStandbyPacket> {
    public static final PacketRecord<TransferStandbyPacket> REC = rec(
            Identifier.fsb("transfer/standby"),
            TransferStandbyPacket::new
    );

    public final int transactionID;

    public TransferStandbyPacket(int transactionID) {
        this.transactionID = transactionID;
    }

    public TransferStandbyPacket(IFriendlyByteBuf buf, Object context) {
        this.transactionID = buf.readInt();
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(transactionID);
    }

    @Override
    public PacketRecord<TransferStandbyPacket> identify() {
        return REC;
    }
}

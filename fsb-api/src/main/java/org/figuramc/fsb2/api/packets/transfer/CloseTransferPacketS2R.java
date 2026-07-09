package org.figuramc.fsb2.api.packets.transfer;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets.PacketRecord;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/**
 * sender -> receiver.
 * the sender will not continue to process this transfer. the transfer session is now over.
 * this is a failure condition if the receiver has not completed the
 * transfer yet, but note that this packet will still be sent in successful cases as well.
 */
public final class CloseTransferPacketS2R implements Packet<CloseTransferPacketS2R> {
    public static final PacketRecord<CloseTransferPacketS2R> REC = rec(
            Identifier.fsb("transfer/close/s2r"),
            CloseTransferPacketS2R::new
    );

    public final int transactionID;

    public CloseTransferPacketS2R(int transactionID) {
        this.transactionID = transactionID;
    }

    public CloseTransferPacketS2R(IFriendlyByteBuf buf, Object context) {
        this.transactionID = buf.readInt();
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(transactionID);
    }

    @Override
    public PacketRecord<CloseTransferPacketS2R> identify() {
        return REC;
    }
}

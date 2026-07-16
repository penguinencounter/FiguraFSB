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
public final class TransferClosePacketS2R implements Packet<TransferClosePacketS2R> {
    public static final PacketRecord<TransferClosePacketS2R> REC = rec(
            Identifier.fsb("transfer/close/s2r"),
            TransferClosePacketS2R::new
    );

    public final int transactionID;

    public TransferClosePacketS2R(int transactionID) {
        this.transactionID = transactionID;
    }

    public TransferClosePacketS2R(IFriendlyByteBuf buf, Object context) {
        this.transactionID = buf.readInt();
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(transactionID);
    }

    @Override
    public PacketRecord<TransferClosePacketS2R> identify() {
        return REC;
    }
}

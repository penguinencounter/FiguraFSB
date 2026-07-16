package org.figuramc.fsb2.api.packets.transfer;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets.PacketRecord;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/**
 * receiver -> sender.
 * the receiver has gotten all the chunks successfully; the transfer session is now over.
 * this is a successful outcome.
 */
public final class TransferClosePacketR2S implements Packet<TransferClosePacketR2S> {
    public static final PacketRecord<TransferClosePacketR2S> REC = rec(
            Identifier.fsb("transfer/close/r2s"),
            TransferClosePacketR2S::new
    );

    public final int transactionID;
    public final boolean successful;

    public TransferClosePacketR2S(int transactionID, boolean successful) {
        this.transactionID = transactionID;
        this.successful = successful;
    }

    public TransferClosePacketR2S(IFriendlyByteBuf buf, Object context) {
        this.transactionID = buf.readInt();
        this.successful = buf.readByte() > 0;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(transactionID);
        buf.writeByte(successful ? 1 : 0);
    }

    @Override
    public PacketRecord<TransferClosePacketR2S> identify() {
        return REC;
    }
}

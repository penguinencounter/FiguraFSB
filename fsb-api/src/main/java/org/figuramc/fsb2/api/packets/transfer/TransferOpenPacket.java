package org.figuramc.fsb2.api.packets.transfer;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets.PacketRecord;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/**
 * sender -> receiver.
 * request to start a transfer.
 */
public final class TransferOpenPacket implements Packet<TransferOpenPacket> {
    public static final PacketRecord<TransferOpenPacket> REC = rec(
            Identifier.fsb("transfer/open"),
            TransferOpenPacket::new
    );
    public final int transactionID;
    public final int totalSize;
    public final int numberOfChunks;
    public final long overallCRC;

    public TransferOpenPacket(int transactionID, int totalSize, int numberOfChunks, long overallCRC) {
        this.transactionID = transactionID;
        this.totalSize = totalSize;
        this.numberOfChunks = numberOfChunks;
        this.overallCRC = overallCRC;
    }

    public TransferOpenPacket(IFriendlyByteBuf buf, Object context) {
        this.transactionID = buf.readInt();
        this.totalSize = buf.readInt();
        this.numberOfChunks = buf.readInt();
        this.overallCRC = buf.readInt() & 0x00000000ffffffffL;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(this.transactionID);
        buf.writeInt(this.totalSize);
        buf.writeInt(this.numberOfChunks);
        buf.writeInt((int) this.overallCRC);
    }

    @Override
    public PacketRecord<TransferOpenPacket> identify() {
        return REC;
    }
}

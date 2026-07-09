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
public final class OpenTransferPacket implements Packet<OpenTransferPacket> {
    public static final PacketRecord<OpenTransferPacket> REC = rec(
            Identifier.fsb("transfer/open"),
            OpenTransferPacket::new
    );
    public final int transactionID;
    public final int totalSize;
    public final int numberOfChunks;

    public OpenTransferPacket(int transactionID, int totalSize, int numberOfChunks) {
        this.transactionID = transactionID;
        this.totalSize = totalSize;
        this.numberOfChunks = numberOfChunks;
    }

    public OpenTransferPacket(IFriendlyByteBuf buf, Object context) {
        this.transactionID = buf.readInt();
        this.totalSize = buf.readInt();
        this.numberOfChunks = buf.readInt();
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(this.transactionID);
        buf.writeInt(this.totalSize);
        buf.writeInt(this.numberOfChunks);
    }

    @Override
    public PacketRecord<OpenTransferPacket> identify() {
        return REC;
    }
}

package org.figuramc.fsb2.api.packets.transfer;

import org.figuramc.fsb2.api.except.FSBInvalidDataException;
import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets.PacketRecord;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/*
+--------+-------+----------+==============+
| txid 4 | CRC 4 | length 2 | data[length] |
+--------+-------+----------+==============+
 */

/**
 * sender -> receiver.
 * transfer data.
 */
public final class TransferChunkPacket implements Packet<TransferChunkPacket> {
    public static final PacketRecord<TransferChunkPacket> REC = rec(
            Identifier.fsb("transfer/chunk"),
            TransferChunkPacket::new
    );

    private final int transactionID;
    private final long chunkCRC;
    private final byte[] data;

    public TransferChunkPacket(int transactionID, long chunkCRC, byte[] data) throws FSBInvalidDataException {
        this.transactionID = transactionID;
        this.chunkCRC = chunkCRC;
        this.data = data;
        if (data.length > 0xffff)
            throw new FSBInvalidDataException("Chunked transfers cannot be longer than 0xFFFF bytes per chunk");
    }

    public TransferChunkPacket(IFriendlyByteBuf buf, Object context) {
        this.transactionID = buf.readInt();
        this.chunkCRC = buf.readInt() & 0x00000000ffffffffL;
        int length = buf.readShort() & 0x0000ffff;
        this.data = buf.readNBytes(length);
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(transactionID);
        buf.writeInt((int) chunkCRC);
        buf.writeShort((short) data.length);
        buf.writeBytes(data);
    }

    @Override
    public PacketRecord<TransferChunkPacket> identify() {
        return REC;
    }
}

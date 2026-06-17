package org.figuramc.fsb2.api.packets.transfer;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets.PacketRecord;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/**
 * sender -> receiver.
 * transfer data.
 */
public final class TransferChunkPacket implements Packet<TransferChunkPacket> {
    public static final PacketRecord<TransferChunkPacket> REC = rec(
            Identifier.figura("transfer/chunk"),
            TransferChunkPacket::new
    );

    public TransferChunkPacket(IFriendlyByteBuf buf) {
        // TODO
    }

    @Override
    public void write(IFriendlyByteBuf buf) {

    }

    @Override
    public PacketRecord<TransferChunkPacket> identify() {
        return REC;
    }
}

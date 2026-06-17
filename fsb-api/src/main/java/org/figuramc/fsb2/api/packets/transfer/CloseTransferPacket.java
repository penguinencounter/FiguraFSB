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
public final class CloseTransferPacket implements Packet<CloseTransferPacket> {
    public static final PacketRecord<CloseTransferPacket> REC = rec(
            Identifier.figura("transfer/close"),
            CloseTransferPacket::new
    );

    public CloseTransferPacket(IFriendlyByteBuf buf) {
        // TODO
    }

    @Override
    public void write(IFriendlyByteBuf buf) {

    }

    @Override
    public PacketRecord<CloseTransferPacket> identify() {
        return REC;
    }
}

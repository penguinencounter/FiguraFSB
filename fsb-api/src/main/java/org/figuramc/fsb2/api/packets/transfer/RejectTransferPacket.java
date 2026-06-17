package org.figuramc.fsb2.api.packets.transfer;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets.PacketRecord;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/**
 * receiver -> sender.
 * the receiver will not (continue to) process this transfer. the transfer session is now over.
 */
public final class RejectTransferPacket implements Packet<RejectTransferPacket> {
    public static final PacketRecord<RejectTransferPacket> REC = rec(
            Identifier.figura("transfer/reject"),
            RejectTransferPacket::new
    );

    public RejectTransferPacket(IFriendlyByteBuf buf) {
        // TODO
    }

    @Override
    public void write(IFriendlyByteBuf buf) {

    }

    @Override
    public PacketRecord<RejectTransferPacket> identify() {
        return REC;
    }
}

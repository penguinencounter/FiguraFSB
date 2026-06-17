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
public final class CompletedTransferPacket implements Packet<CompletedTransferPacket> {
    public static final PacketRecord<CompletedTransferPacket> REC = rec(
            Identifier.figura("transfer/complete"),
            CompletedTransferPacket::new
    );

    public CompletedTransferPacket(IFriendlyByteBuf buf) {
        // TODO
    }

    @Override
    public void write(IFriendlyByteBuf buf) {

    }

    @Override
    public PacketRecord<CompletedTransferPacket> identify() {
        return REC;
    }
}

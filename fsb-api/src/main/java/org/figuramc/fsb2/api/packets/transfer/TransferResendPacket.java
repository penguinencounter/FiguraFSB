package org.figuramc.fsb2.api.packets.transfer;

import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets.PacketRecord;
import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/**
 * receiver -> sender.
 * the receiver is still missing some chunks and wants the client to send them
 * again in order to complete the transfer.
 */
public final class TransferResendPacket implements Packet<TransferResendPacket> {
    public static final PacketRecord<TransferResendPacket> REC = rec(
            Identifier.figura("transfer/resend"),
            TransferResendPacket::new
    );

    public TransferResendPacket(IFriendlyByteBuf buf) {
        // TODO
    }

    @Override
    public void write(IFriendlyByteBuf buf) {

    }

    @Override
    public PacketRecord<TransferResendPacket> identify() {
        return REC;
    }
}

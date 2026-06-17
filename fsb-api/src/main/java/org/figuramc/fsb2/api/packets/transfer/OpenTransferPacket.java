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
            Identifier.figura("transfer/open"),
            OpenTransferPacket::new
    );

    public OpenTransferPacket(IFriendlyByteBuf buf) {
        // TODO
    }

    @Override
    public void write(IFriendlyByteBuf buf) {

    }

    @Override
    public PacketRecord<OpenTransferPacket> identify() {
        return REC;
    }
}

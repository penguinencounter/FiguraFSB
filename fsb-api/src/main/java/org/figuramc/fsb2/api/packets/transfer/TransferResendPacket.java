package org.figuramc.fsb2.api.packets.transfer;

import org.figuramc.fsb2.api.ProtocolSession;
import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets.PacketRecord;
import org.figuramc.fsb2.api.transfer.TransferOutbox;
import org.figuramc.fsb2.api.utils.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/**
 * receiver -> sender.
 * the receiver is still missing some chunks and wants the client to send them
 * again in order to complete the transfer.
 */
public final class TransferResendPacket implements Packet<TransferResendPacket> {
    public static final PacketRecord<TransferResendPacket> REC = rec(
            Identifier.fsb("transfer/resend"),
            TransferResendPacket::new
    );

    public final int transactionID;
    /**
     * {@code null} if packet decode failed because there were too many chunks, or the transaction wasn't recognized.
     * Should never be {@code null} when sending the packet.
     */
    public final @Nullable List<Integer> neededChunks;

    /**
     * RECEIVER SIDE
     */
    public TransferResendPacket(int transactionID, Collection<Integer> neededChunks) {
        this.transactionID = transactionID;
        this.neededChunks = Collections.unmodifiableList(new ArrayList<>(neededChunks));
    }

    public TransferResendPacket(int transactionID, IntStream stream) {
        this.transactionID = transactionID;
        this.neededChunks = Collections.unmodifiableList(stream.boxed().collect(Collectors.toList()));
    }

    /**
     * SENDER SIDE
     */
    public TransferResendPacket(IFriendlyByteBuf buf, Object context) {
        this.transactionID = buf.readInt();
        ArrayList<Integer> req = new ArrayList<>();
        int n = buf.readInt();

        ProtocolSession currentSession = ProtocolSession.lookup(context);
        if (currentSession == null) {
            this.neededChunks = null;
            return;
        }
        TransferOutbox target = currentSession.outboxFor(transactionID);
        if (target == null || n > target.totalNumberOfChunks) {
            this.neededChunks = null;
            return;
        }

        for (int i = 0; i < n; i++) req.add(buf.readInt());

        neededChunks = Collections.unmodifiableList(req);
    }

    /**
     * RECEIVER SIDE
     */
    @Override
    public void write(IFriendlyByteBuf buf) {
        assert neededChunks != null;
        buf.writeInt(neededChunks.size());
        for (int chunk : neededChunks) buf.writeInt(chunk);
    }

    @Override
    public PacketRecord<TransferResendPacket> identify() {
        return REC;
    }
}

package org.figuramc.fsb2.api;

import org.figuramc.fsb2.api.except.FSBException;
import org.figuramc.fsb2.api.except.FSBStateException;
import org.figuramc.fsb2.api.packets.transfer.CloseTransferPacketS2R;
import org.figuramc.fsb2.api.packets.transfer.TransferChunkPacket;
import org.figuramc.fsb2.api.transfer.TransferInbox;

public class BuiltInHandlers {
    public static void setupTransferHandling(ProtocolSession session) {
        try {
            session.onReceive(TransferChunkPacket.REC, (p, c) -> {
                try {
                    PlayerSession remote = session.getRemote(c);
                    TransferInbox inbox = session.inboxFor(remote.sessionID, p.transactionID);
                    if (inbox == null) throw new NullPointerException("No inbox for this session + transaction");
                    inbox.receive(p.chunkID, p.data, false);
                } catch (FSBException | RuntimeException e) {
                    session.logger.warn("Failed to handle transfer chunk", e);
                }
            });
            session.onReceive(CloseTransferPacketS2R.REC, (p, c) -> {
                try {
                    PlayerSession remote = session.getRemote(c);
                    TransferInbox inbox = session.inboxFor(remote.sessionID, p.transactionID);
                    if (inbox == null) throw new NullPointerException("No inbox for this session + transaction");
                    if (inbox.getState() == TransferInbox.State.OPEN)
                        inbox.maybeReject("Client closed the transfer");

                } catch (FSBException | RuntimeException e) {
                    session.logger.warn("Failed to handle transfer chunk", e);
                }
            });
        } catch (FSBStateException e) {
            throw new RuntimeException(e);
        }
    }
}

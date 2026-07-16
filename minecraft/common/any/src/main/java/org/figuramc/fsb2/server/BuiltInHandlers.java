package org.figuramc.fsb2.server;

import org.figuramc.fsb2.api.PlayerSession;
import org.figuramc.fsb2.api.ProtocolSession;
import org.figuramc.fsb2.api.except.FSBArgumentException;
import org.figuramc.fsb2.api.except.FSBException;
import org.figuramc.fsb2.api.except.FSBStateException;
import org.figuramc.fsb2.api.packets.transfer.*;
import org.figuramc.fsb2.api.transfer.TransferInbox;
import org.figuramc.fsb2.api.transfer.TransferOutbox;
import org.figuramc.fsb2.server.internals.NetworkingService;

public class BuiltInHandlers {
    public static void setupTransferHandling(ProtocolSession session) {
        try {
            // S2R
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
            session.onReceive(TransferClosePacketS2R.REC, (p, c) -> {
                try {
                    PlayerSession remote = session.getRemote(c);
                    TransferInbox inbox = session.inboxFor(remote.sessionID, p.transactionID);
                    if (inbox == null) throw new NullPointerException("No inbox for this session + transaction");
                    if (inbox.getState() == TransferInbox.State.OPEN)
                        inbox.maybeReject("Client closed the transfer early");

                } catch (FSBException | RuntimeException e) {
                    session.logger.warn("Failed to handle transfer close", e);
                }
            });
            session.onReceive(TransferStandbyPacket.REC, (p, c) -> {
                // This packet prompts a resend response, if needed.
                try {
                    PlayerSession remote = session.getRemote(c);
                    TransferInbox inbox = session.inboxFor(remote.sessionID, p.transactionID);
                    if (inbox == null) throw new NullPointerException("No inbox for this session + transaction");

                    TransferResendPacket packet = inbox.produceResend();
                    if (packet != null) NetworkingService.SERVICE.sendUnchecked(c, packet);
                } catch (FSBException | RuntimeException e) {
                    session.logger.warn("Failed to handle transfer standby", e);
                }
            });
            session.onReceive(TransferOpenPacket.REC, (p, c) -> {
                // If we don't have something valid, just echo the same ID back in a rejection.
                goAway:
                {
                    try {
                        PlayerSession remote = session.getRemote(c);
                        TransferInbox inbox = session.inboxFor(remote.sessionID, p.transactionID);
                        if (inbox == null) {
                            session.logger.warn(
                                    "Failed transfer open attempt (remote {} -> {}, {}), nothing to write chunks into",
                                    c, remote, p.transactionID
                                    );
                            break goAway;
                        }
                        // TODO: add hook
                        NetworkingService.SERVICE.sendUnchecked(c, new TransferAcceptPacket(p.transactionID));
                        return;
                    } catch (FSBArgumentException e) {
                        session.logger.warn(
                                "Failed transfer open attempt (remote {}, {}), missing remote registration",
                                c, p.transactionID
                        );
                        // Fall through
                    }
                }
                // go away, maybe?
                if (!NetworkingService.SERVICE.trySend(c, new TransferClosePacketR2S(p.transactionID, false)))
                    session.logger.warn(
                            "transfer rejected and the connection object wasn't valid (was {})",
                            c.getClass().getName()
                    );
            });

            // R2S
            session.onReceive(TransferAcceptPacket.REC, (p, c) -> {
                try {
                    TransferOutbox outbox = session.outboxFor(p.transactionID);
                    if (outbox == null) throw new NullPointerException("No outbox for this transaction");
                    outbox.accepted();
                } catch (FSBStateException e) {
                    session.logger.error("Failed to handle transfer accept", e);
                }
            });
        } catch (FSBStateException e) {
            throw new RuntimeException(e);
        }
    }
}

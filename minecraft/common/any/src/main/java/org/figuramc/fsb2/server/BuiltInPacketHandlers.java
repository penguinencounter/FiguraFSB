package org.figuramc.fsb2.server;

import org.figuramc.fsb2.api.PlayerSession;
import org.figuramc.fsb2.api.except.FSBArgumentException;
import org.figuramc.fsb2.api.except.FSBException;
import org.figuramc.fsb2.api.except.FSBStateException;
import org.figuramc.fsb2.api.packets.transfer.*;
import org.figuramc.fsb2.api.transfer.TransferInbox;
import org.figuramc.fsb2.api.transfer.TransferOutbox;
import org.figuramc.fsb2.server.internals.NetworkingService;

public class BuiltInPacketHandlers {
    private final ServerSession session;

    BuiltInPacketHandlers(ServerSession session) {
        this.session = session;
    }

    private void failToHandle(String description, Throwable e) {
        session.logger.warn("Failed to handle " + description, e);
    }

    void setup() {
        try {
            // S2R
            // TODO: Tell client something bad happened if one of these failed and we have enough info for that
            session.onReceive(TransferChunkPacket.REC, this::handleTransferChunkPacket);
            session.onReceive(TransferClosePacketS2R.REC, this::handleTransferClosePacketS2R);
            session.onReceive(TransferStandbyPacket.REC, this::handleTransferStandbyPacket);
            session.onReceive(TransferOpenPacket.REC, this::handleTransferOpenPacket);

            // R2S
            session.onReceive(TransferAcceptPacket.REC, this::handleTransferAcceptPacket);
        } catch (FSBStateException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleTransferChunkPacket(TransferChunkPacket p, Object c) {
        try {
            PlayerSession remote = session.getRemote(c);
            TransferInbox inbox = session.existingInboxFor(remote.sessionID, p.transactionID);
            if (inbox == null) throw new NullPointerException("No inbox for this session + transaction");
            inbox.receive(p.chunkID, p.data, false);
        } catch (FSBException | RuntimeException e) {
            failToHandle("transfer chunk", e);
        }
    }

    private void handleTransferClosePacketS2R(TransferClosePacketS2R p, Object c) {
        try {
            PlayerSession remote = session.getRemote(c);
            TransferInbox inbox = session.existingInboxFor(remote.sessionID, p.transactionID);
            if (inbox == null) throw new NullPointerException("No inbox for this session + transaction");
            if (inbox.getState() == TransferInbox.State.OPEN)
                inbox.maybeReject("Client closed the transfer early");

        } catch (FSBException | RuntimeException e) {
            failToHandle("transfer close", e);
        }
    }

    private void handleTransferStandbyPacket(TransferStandbyPacket p, Object c) {
        // This packet prompts a resend response, if needed.
        try {
            PlayerSession remote = session.getRemote(c);
            TransferInbox inbox = session.existingInboxFor(remote.sessionID, p.transactionID);
            if (inbox == null) throw new NullPointerException("No inbox for this session + transaction");

            TransferResendPacket packet = inbox.produceResend();
            if (packet != null) NetworkingService.SERVICE.sendUnchecked(c, packet);
        } catch (FSBException | RuntimeException e) {
            failToHandle("transfer standby", e);
        }
    }

    private void handleTransferOpenPacket(TransferOpenPacket p, Object c) {
        // TODO: do we need another try-catch here?
        goAway:
        {
            try {
                PlayerSession remote = session.getRemote(c);
                session.eventBus.BEFORE_TRANSFER.dispatch(new FSBServerEvents.TransferParametersEvent(p, c), session);
                TransferInbox inbox = session.createInboxFor(remote.sessionID, p.transactionID, localID -> new TransferInbox(
                        remote.sessionID, p.transactionID, localID, p.totalSize, p.numberOfChunks, p.overallCRC
                ));
                if (inbox == null) {
                    session.logger.warn(
                            "Failed transfer open attempt (remote {} -> {}, {}), nothing to write chunks into",
                            c, remote, p.transactionID
                    );
                    break goAway;
                }
                NetworkingService.SERVICE.sendUnchecked(c, new TransferAcceptPacket(p.transactionID));
                return;
            } catch (FSBArgumentException e) {
                session.logger.warn(
                        "Failed transfer open attempt (remote {}, {}), missing remote registration",
                        c, p.transactionID
                );
                // Fall through to reject
            } catch (FSBStateException e) {
                session.logger.warn(
                        "Failed transfer open attempt (remote {}, {}), already opened",
                        c, p.transactionID
                );
                // Fall through to reject
            }
        }
        // If we don't have something valid, just echo the same ID back in a rejection.
        if (!NetworkingService.SERVICE.trySend(c, new TransferClosePacketR2S(p.transactionID, false)))
            session.logger.warn(
                    "transfer rejected and the connection object wasn't valid (was {})",
                    c.getClass().getName()
            );
    }

    private void handleTransferAcceptPacket(TransferAcceptPacket p, Object c) {
        try {
            TransferOutbox outbox = session.outboxFor(p.transactionID);
            if (outbox == null) throw new NullPointerException("No outbox for this transaction");
            outbox.accepted();
        } catch (FSBStateException e) {
            failToHandle("transfer accept", e);
        }
    }
}

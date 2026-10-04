package org.figuramc.fsb2.server.uploading;

import com.google.common.collect.MapMaker;
import org.figuramc.fsb2.api.PlayerSession;
import org.figuramc.fsb2.api.except.FSBStateException;
import org.figuramc.fsb2.api.packets.c2s.C2SBeginUploadPacket;

import java.util.concurrent.ConcurrentMap;

/**
 * Keeps track of ongoing uploads, among other things.
 */
public class UploadManager {
    /**
     * maps transfer ID -> uploads
     */
    private final ConcurrentMap<Integer, UploadTicket> ONGOING_UPLOADS = new MapMaker().weakValues().makeMap();

    public void register(int id, C2SBeginUploadPacket packet, PlayerSession session) throws FSBStateException {
        UploadTicket check = ONGOING_UPLOADS.putIfAbsent(id, new UploadTicket(packet, session.sessionID));
        if (check != null) throw new FSBStateException(String.format(
                "already got that ID %d registered (this has gotta be OUR inbox id, not the remote's)", id
        ));
    }
}

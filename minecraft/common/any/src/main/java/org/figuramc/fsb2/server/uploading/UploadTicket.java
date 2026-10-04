package org.figuramc.fsb2.server.uploading;

import org.figuramc.fsb2.api.packets.c2s.C2SBeginUploadPacket;

public class UploadTicket {
    public final int transferId;
    public final int remote;
    public final String slot;

    public UploadTicket(C2SBeginUploadPacket packet, int remote) {
        this.transferId = packet.transferId;
        this.slot = packet.avatarSlot;
        this.remote = remote;
    }
}

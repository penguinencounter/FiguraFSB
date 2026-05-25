package org.figuramc.fsb.api.packets.c2s;

import org.figuramc.fsb.api.packets.Packet;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;
import org.figuramc.fsb.api.utils.Identifier;

/**
 * Packet sent to server to acknowledge that client allows server to work as Figura backend.
 */
public class C2SBackendHandshakePacket implements Packet {
    public static final Identifier PACKET_ID = new Identifier("figura", "c2s/handshake");

    public C2SBackendHandshakePacket() {

    }


    @Override
    public void write(IFriendlyByteBuf byteBuf) {

    }

    @Override
    public Identifier getId() {
        return PACKET_ID;
    }
}
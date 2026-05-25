package org.figuramc.fsb.api.packets.c2s;

import org.figuramc.fsb.api.packets.Packet;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;
import org.figuramc.fsb.api.utils.Identifier;

/**
 * Packet sent to server in order to request all owned avatars.
 * Can't be sent by current Figura client v0.1.4 and less, so it is basically made just for future versions.
 */
public class C2SFetchOwnedAvatarsPacket implements Packet {
    public static final Identifier PACKET_ID = new Identifier("figura", "c2s/avatars/owned");
    @Override
    public void write(IFriendlyByteBuf buf) {

    }

    @Override
    public Identifier getId() {
        return null;
    }
}

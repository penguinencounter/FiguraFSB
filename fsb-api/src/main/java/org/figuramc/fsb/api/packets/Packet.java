package org.figuramc.fsb.api.packets;

import org.figuramc.fsb.api.utils.IFriendlyByteBuf;
import org.figuramc.fsb.api.utils.Identifier;

public interface Packet {
    void write(IFriendlyByteBuf buf);

    Identifier getId();


    interface Deserializer<P extends Packet> {
        P read(IFriendlyByteBuf buf);
    }
}

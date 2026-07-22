package org.figuramc.fsb2.api.packets;

public interface Encodable {
    void write(IFriendlyByteBuf buf);
}

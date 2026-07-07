package org.figuramc.fsb2.api.packets;

import org.figuramc.fsb2.api.except.FSBException;

public interface Packet<T extends Packet<?>> {
    void write(IFriendlyByteBuf buf);

    Packets.PacketRecord<T> identify();

    @FunctionalInterface
    interface Deserializer<P extends Packet<?>> {
        P read(IFriendlyByteBuf buf, Object context) throws FSBException;
    }
}

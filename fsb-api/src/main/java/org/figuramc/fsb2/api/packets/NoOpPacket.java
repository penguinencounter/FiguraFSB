package org.figuramc.fsb2.api.packets;

import org.figuramc.fsb2.api.utils.Identifier;

import static org.figuramc.fsb2.api.packets.Packets.PacketRecord.rec;

/**
 * decoding errors fall here. don't make this yourself.
 */
public class NoOpPacket implements Packet<NoOpPacket> {
    public static final Packets.PacketRecord<NoOpPacket> REC = rec(
            Identifier.fsb("noop"),
            NoOpPacket::new
    );

    /**
     * Despite the name, this class is not a singleton. You can have other instances of it through
     * the two-argument constructor.
     * <p>
     * However, all instances of this class are equal.
     */
    public static final NoOpPacket INSTANCE = new NoOpPacket();

    private NoOpPacket() {}

    public NoOpPacket(IFriendlyByteBuf buf, Object context) {}

    @Override
    public void write(IFriendlyByteBuf buf) {}

    @Override
    public Packets.PacketRecord<NoOpPacket> identify() {
        return REC;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return 0x3CC85342;
    }
}

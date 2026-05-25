package org.figuramc.fsb.api.packets.s2c;

import org.figuramc.fsb.api.packets.Packet;
import org.figuramc.fsb.api.utils.Hash;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;
import org.figuramc.fsb.api.utils.Identifier;

public class S2CInitializeAvatarStreamPacket implements Packet {
    public static final Identifier PACKET_ID = new Identifier("figura", "s2c/stream/init");

    private final int streamId;
    private final Hash hash;
    private final Hash ehash;

    public S2CInitializeAvatarStreamPacket(int streamId, Hash hash, Hash ehash) {
        this.streamId = streamId;
        this.hash = hash;
        this.ehash = ehash;
    }

    public S2CInitializeAvatarStreamPacket(IFriendlyByteBuf buf) {
        streamId = buf.readInt();
        hash = buf.readHash();
        ehash = buf.readHash();
    }

    public int streamId() {
        return streamId;
    }

    public Hash ehash() {
        return ehash;
    }

    @Override
    public void write(IFriendlyByteBuf buf) {
        buf.writeInt(streamId);
        buf.writeBytes(hash.get());
        buf.writeBytes(ehash.get());
    }

    @Override
    public Identifier getId() {
        return PACKET_ID;
    }
}

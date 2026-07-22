package org.figuramc.fsb2.api.config;

import org.figuramc.fsb2.api.packets.Encodable;
import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

// I wish we had records ;_;
public class ServerIdentification implements Encodable {
    /**
     * Is this object filled?
     */
    public boolean isPopulated = false;

    /**
     * Text that goes in the MOTD box in the menu.
     */
    public String motd;

    // Limitations
    public long maxUploadSize;      // bytes
    public int maxNumberOfAvatars;  // simultaneous uploaded
    public int maxPingSize;         // bytes
    public int maxPingRate;         // pings per second

    // Feature enablement
    /**
     * Enables uploading avatars to the server, using the limits defined
     * in {@link #maxUploadSize} and {@link #maxNumberOfAvatars}.
     */
    public boolean supportsUploading;
    /**
     * Must be {@code true} if {@link #supportsUploading} is {@code true}.
     * <p>
     * Enables fetching avatars from the server.
     */
    public boolean supportsDownloading;
    /**
     * Enables proxying pings. If disabled, clients will use the global cloud for pings.
     */
    public boolean supportsPings;
    /**
     * Enables the {@code server_packets} API on compliant clients.
     */
    public boolean supportsCustomPackets;

    // Individual permission bits
    /**
     * Can edit default permission settings and feature flags remotely.
     */
    public boolean canManageServer;
    /**
     * Can view and delete others' uploaded avatars, ban players from uploading or connecting, etc.
     */
    public boolean canModerate;
    /**
     * Can convert an avatar into an immortalized one.
     */
    public boolean canImmortalize;

    // Server policy
    /**
     * Allow loading avatars from the global cloud.
     * If disabled, compliant clients will avoid downloading avatars from the global cloud.
     * Avatars from alternate clouds will use global cloud services for pings.
     */
    public boolean allowMixingAvatars;

    public ServerIdentification() {}

    public void write(IFriendlyByteBuf buf) {
        buf.writeLong(maxUploadSize);
        buf.writeInt(maxNumberOfAvatars);
        buf.writeInt(maxPingSize);
        buf.writeInt(maxPingRate);
        buf.writeByte(packBitflags());
        buf.writeByteArray(motd.getBytes(StandardCharsets.UTF_8));
    }

    private byte packBitflags() {
        BitSet bitSet = new BitSet(8);
        bitSet.set(0, this.supportsUploading);
        bitSet.set(1, this.supportsDownloading);
        bitSet.set(2, this.supportsPings);
        bitSet.set(3, this.supportsCustomPackets);
        bitSet.set(4, this.allowMixingAvatars);
        bitSet.set(5, this.canManageServer);
        bitSet.set(6, this.canModerate);
        bitSet.set(7, this.canImmortalize);
        return bitSet.toByteArray()[0];
    }

    private void unpackBitflags(byte flags) {
        byte[] array = {flags};
        BitSet bitset = BitSet.valueOf(array);
        this.supportsUploading = bitset.get(0);
        this.supportsDownloading = bitset.get(1);
        this.supportsPings = bitset.get(2);
        this.supportsCustomPackets = bitset.get(3);
        this.allowMixingAvatars = bitset.get(4);
        this.canManageServer = bitset.get(5);
        this.canModerate = bitset.get(6);
        this.canImmortalize = bitset.get(7);
    }

    public static ServerIdentification decode(IFriendlyByteBuf buf) {
        ServerIdentification sid = new ServerIdentification();
        sid.maxUploadSize = buf.readLong();
        sid.maxNumberOfAvatars = buf.readInt();
        sid.maxPingSize = buf.readInt();
        sid.maxPingRate = buf.readInt();
        sid.unpackBitflags(buf.readByte());
        sid.motd = new String(buf.readByteArray(0x10000), StandardCharsets.UTF_8);

        sid.isPopulated = true;
        return sid;
    }
}

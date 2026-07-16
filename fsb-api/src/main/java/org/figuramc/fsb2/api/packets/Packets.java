package org.figuramc.fsb2.api.packets;

import org.figuramc.fsb2.api.ProtocolSession;
import org.figuramc.fsb2.api.except.FSBException;
import org.figuramc.fsb2.api.packets.s2c.S2CAdvertisePacket;
import org.figuramc.fsb2.api.packets.transfer.*;
import org.figuramc.fsb2.api.utils.Identifier;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public final class Packets {

    public static class PacketRecord<T extends Packet<?>> {
        @NotNull
        public final Identifier id;
        public final Packet.Deserializer<T> deserializer;

        public PacketRecord(@NotNull Identifier id, Packet.Deserializer<T> deserializer) {
            this.id = id;
            this.deserializer = deserializer;
        }

        /**
         * you can {@code import static} this and use it as the constructor instead
         * (it's shorter)
         */
        @Contract("_, _ -> new")
        public static <T extends Packet<?>> @NotNull PacketRecord<T> rec(Identifier id, Packet.Deserializer<T> deserializer) {
            return new PacketRecord<>(id, deserializer);
        }
    }

    private static final HashMap<Identifier, PacketRecord<?>> allPackets = new HashMap<>();

    public static <T extends Packet<?>> void register(PacketRecord<T> instance) {
        allPackets.put(instance.id, instance);
    }

    public static PacketRecord<?> getRecord(Identifier id) {
        return allPackets.get(id);
    }

    @CheckReturnValue
    public static Packet<?> decode(@NotNull IFriendlyByteBuf buf, @NotNull Object context) {
        Identifier id = Identifier.parse(new String(buf.readByteArray(512), StandardCharsets.UTF_8));
        PacketRecord<?> record = getRecord(id);
        if (record == null) return NoOpPacket.INSTANCE;
        try {
            return record.deserializer.read(buf, context);
        } catch (FSBException e) {
            // Try to acquire a logger and complain.
            ProtocolSession maybeSession = ProtocolSession.lookup(context);
            if (maybeSession == null) return NoOpPacket.INSTANCE;
            maybeSession.logger.error(String.format("Decode rejected for FSB packet '%s' (ctx %s), dropping. reason:", id, context), e);
            return NoOpPacket.INSTANCE;
        }
    }

    public static void dispatchPacket(@NotNull Packet<?> packet, @NotNull Object context) {
        ProtocolSession session = ProtocolSession.lookup(context);
        if (session == null) return;
        session.handlePacket(packet, context);
    }

    static {
        // chunked transfer machinery (.transfer)
        register(TransferOpenPacket.REC);
        register(TransferAcceptPacket.REC);
        register(TransferChunkPacket.REC);
        register(TransferResendPacket.REC);
        register(TransferStandbyPacket.REC);
        register(TransferClosePacketR2S.REC);
        register(TransferClosePacketS2R.REC);

        // .s2c
        register(S2CAdvertisePacket.REC);
    }
}

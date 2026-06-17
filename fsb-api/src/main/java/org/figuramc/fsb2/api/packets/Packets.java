package org.figuramc.fsb2.api.packets;

import org.figuramc.fsb2.api.packets.transfer.*;
import org.figuramc.fsb2.api.utils.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class Packets {
    public static class PacketRecord<T extends Packet<?>> {
        @NotNull
        public final Identifier id;
        public final int hashId;

        public PacketRecord(Identifier id, Packet.Deserializer<T> deserializer) {
            this.id = id;
            this.hashId = id.toString().hashCode();
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

    private static final HashMap<Integer, PacketRecord<?>> allPackets = new HashMap<>();

    public static <T extends Packet<?>> void register(PacketRecord<T> instance) {
        if (allPackets.containsKey(instance.hashId)) {
            throw new AssertionError(String.format("duplicate ID, or hash collision! %s => %08x", instance.id, instance.hashId));
        }
        allPackets.put(instance.hashId, instance);
    }

    static {
        // chunked transfer machinery (.transfer)
        register(OpenTransferPacket.REC);
        register(AcceptTransferPacket.REC);
        register(TransferChunkPacket.REC);
        register(TransferResendPacket.REC);
        register(TransferStandbyPacket.REC);
        register(CompletedTransferPacket.REC);
        register(RejectTransferPacket.REC);
        register(CloseTransferPacket.REC);
    }
}

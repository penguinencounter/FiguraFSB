package org.figuramc.fsb2.server.versioned;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.server.FSB;
import org.figuramc.fsb2.services.FSBNetworkingService;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public interface VersionedNetworking extends FSBNetworkingService<ServerGamePacketListenerImpl> {
    default void sendVia(@NotNull Consumer<? super ClientboundCustomPayloadPacket> consumer, @NotNull Packet<?> packet) {
        ServerPacketImpl.Buf bufW = new ServerPacketImpl.Buf(new FriendlyByteBuf(Unpooled.buffer()));
        bufW.writeByteArray(packet.identify().netID);
        packet.write(bufW);
        consumer.accept(new ClientboundCustomPayloadPacket(ServerPacketImpl.PACKET_ID, bufW.actual()));
    }

    @Override
    default void send(@NotNull ServerGamePacketListenerImpl connection, @NotNull Packet<?> packet) {
        sendVia(connection::send, packet);
    }

    @Override
    default void sendToPlayer(Object minecraftServer, @NotNull UUID player, @NotNull Packet<?> packet) {
        if (!(minecraftServer instanceof MinecraftServer srv)) throw new IllegalArgumentException("minecraftServer is not a MinecraftServer");
        srv.execute(() -> {
            ServerPlayer actualPlayer = srv.getPlayerList().getPlayer(player);
            if (actualPlayer == null) {
                FSB.LOGGER.warn("Tried to send a packet to {}, but that player was not found", player);
            } else {
                send(actualPlayer.connection, packet);
            }
        });
    }

    @Override
    default boolean trySend(Object maybeConnection, @NotNull Packet<?> packet) {
        if (maybeConnection instanceof ServerGamePacketListenerImpl) {
            send((ServerGamePacketListenerImpl) maybeConnection, packet);
            return true;
        } else return false;
    }
}

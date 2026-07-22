package org.figuramc.fsb2.server.fabric;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.server.FSB;
import org.figuramc.fsb2.server.FSBWrapper;
import org.figuramc.fsb2.server.ServerPacketImpl;
import org.figuramc.fsb2.server.VersionedNetworking;
import org.figuramc.fsb2.services.FSBInitializerService;
import org.figuramc.fsb2.services.FSBNetworkingService;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FabricNetworkingImpl implements VersionedNetworking, FSBInitializerService {
    @Override
    public void send(@NotNull ServerGamePacketListenerImpl connection, @NotNull Packet<?> packet) {
        ServerPacketImpl.Buf bufW = new ServerPacketImpl.Buf(Unpooled.buffer());
        packet.write(bufW);
        FSBWrapper wrapperPayload = new FSBWrapper(bufW);
        connection.send(ServerPlayNetworking.createS2CPacket(wrapperPayload));
    }

    @Override
    public void sendToPlayer(Object minecraftServer, @NotNull UUID player, @NotNull Packet<?> packet) {
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
    public boolean trySend(Object maybeConnection, @NotNull Packet<?> packet) {
        if (maybeConnection instanceof ServerGamePacketListenerImpl) {
            send((ServerGamePacketListenerImpl) maybeConnection, packet);
            return true;
        } else return false;
    }

    private static void fsbDispatcher(FSBWrapper data, ServerPlayNetworking.Context context) {
        ServerGamePacketListenerImpl relevant = context.player().connection;
        Packet<?> decode = Packets.decode(data.packagedBuffer(), relevant);
        Packets.dispatchPacket(decode, relevant);
    }

    @Override
    public void init() {
        PayloadTypeRegistry.playS2C().register(FSBWrapper.TYPE, FSBWrapper.CODEC);
        PayloadTypeRegistry.playC2S().register(FSBWrapper.TYPE, FSBWrapper.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(FSBWrapper.TYPE, FabricNetworkingImpl::fsbDispatcher);
    }

    @Override
    public int priority() {
        return 5;
    }
}

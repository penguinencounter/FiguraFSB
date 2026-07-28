package org.figuramc.fsb2.fabric.versioned;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.server.versioned.ServerPacketImpl;
import org.figuramc.fsb2.server.versioned.VersionedNetworking;
import org.figuramc.fsb2.services.FSBInitializerService;

public class FabricNetworkingImpl implements FSBInitializerService, VersionedNetworking {
    private static void fsbDispatcher(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerPacketImpl.Buf bufW = new ServerPacketImpl.Buf(buf);
        Packet<?> decode = Packets.decode(bufW, handler);
        Packets.dispatchPacket(decode, handler);
    }

    @Override
    public void init() {
        ServerPlayNetworking.registerGlobalReceiver(ServerPacketImpl.PACKET_ID, FabricNetworkingImpl::fsbDispatcher);
    }

    @Override
    public int priority() {
        return 5;
    }
}

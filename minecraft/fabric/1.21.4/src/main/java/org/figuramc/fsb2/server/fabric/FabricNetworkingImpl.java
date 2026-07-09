package org.figuramc.fsb2.server.fabric;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.figuramc.fsb2.ServerPacketHandler;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.server.FSBWrapper;
import org.figuramc.fsb2.services.FSBInitializerService;
import org.figuramc.fsb2.services.FSBNetworkingService;

public class FabricNetworkingImpl implements FSBNetworkingService<ServerGamePacketListenerImpl>, FSBInitializerService {
    @Override
    public void send(ServerGamePacketListenerImpl connection, Packet<?> packet) {

    }

    @Override
    public <T extends Packet<?>> void onReceive(Packets.PacketRecord<T> packetType, ServerPacketHandler<ServerGamePacketListenerImpl, T> handler) {

    }

    private static void fsbDispatcher(FSBWrapper data, ServerPlayNetworking.Context context) {
        Packet<?> decode = Packets.decode(data.packagedBuffer(), context.player().connection);

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

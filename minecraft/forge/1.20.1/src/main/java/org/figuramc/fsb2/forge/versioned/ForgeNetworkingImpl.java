package org.figuramc.fsb2.forge.versioned;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
import org.figuramc.fsb2.api.FSBConstants;
import org.figuramc.fsb2.api.packets.Packet;
import org.figuramc.fsb2.api.packets.Packets;
import org.figuramc.fsb2.server.versioned.ServerPacketImpl;
import org.figuramc.fsb2.server.versioned.VersionedNetworking;
import org.figuramc.fsb2.services.FSBInitializerService;

import java.util.Objects;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = FSBConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeNetworkingImpl implements VersionedNetworking, FSBInitializerService {
    public void init() {
        EventNetworkChannel chan = NetworkRegistry.newEventChannel(
                ServerPacketImpl.PACKET_ID,
                () -> NetworkRegistry.ACCEPTVANILLA,
                NetworkRegistry.acceptMissingOr(NetworkRegistry.ACCEPTVANILLA),
                NetworkRegistry.acceptMissingOr(NetworkRegistry.ACCEPTVANILLA)
        );
        chan.addListener(consumer);
    }

    /**
     * Logical server only.
     */
    private final Consumer<NetworkEvent> consumer = event -> {
        if (event.getPayload() == null) return;
        NetworkEvent.Context ctx = event.getSource().get();
        if (ctx.getDirection().equals(NetworkDirection.PLAY_TO_SERVER)) {
            ServerGamePacketListenerImpl handler = Objects.requireNonNull(
                    ctx.getSender(),
                    "packet from nowhere?"
            ).connection;
            ServerPacketImpl.Buf bufW = new ServerPacketImpl.Buf(event.getPayload());
            Packet<?> decode = Packets.decode(bufW, handler);
            Packets.dispatchPacket(decode, handler);
            ctx.setPacketHandled(true);
        }
    };
}

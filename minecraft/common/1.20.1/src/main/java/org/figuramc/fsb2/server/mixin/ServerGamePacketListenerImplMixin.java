package org.figuramc.fsb2.server.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.figuramc.fsb2.api.PlayerInfo;
import org.figuramc.fsb2.api.except.FSBArgumentException;
import org.figuramc.fsb2.api.packets.s2c.S2CHelloPacket;
import org.figuramc.fsb2.server.FSB;
import org.figuramc.fsb2.server.ServerExt;
import org.figuramc.fsb2.server.VersionedNetworking;
import org.figuramc.fsb2.server.internals.NetworkingService;
import org.figuramc.fsb2.services.FSBNetworkingService;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void afterPlayBegins(MinecraftServer server, Connection connection, ServerPlayer player, CallbackInfo ci) {
        ServerGamePacketListenerImpl that = (ServerGamePacketListenerImpl) (Object) this;
        ServerExt srv = FSB.serverGet(server);
        PlayerInfo info = new PlayerInfo(player.getUUID(), player.getName().getString());
        try {
            srv.session.newRemote(that, info);

            ((VersionedNetworking) NetworkingService.SERVICE).send(that, new S2CHelloPacket(null));
        } catch (FSBArgumentException ignored) {

        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void beforeDisconnect(Component reason, CallbackInfo ci) {
        ServerGamePacketListenerImpl that = (ServerGamePacketListenerImpl) (Object) this;
        ServerExt srv = FSB.serverGet(server);
        srv.session.delRemote(that);
    }
}

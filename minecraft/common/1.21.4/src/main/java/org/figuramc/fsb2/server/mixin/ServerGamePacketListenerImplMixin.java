package org.figuramc.fsb2.server.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.figuramc.fsb2.FSB;
import org.figuramc.fsb2.ServerExt;
import org.figuramc.fsb2.api.PlayerInfo;
import org.figuramc.fsb2.api.except.FSBArgumentException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("MixinSuperClass")
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin extends ServerCommonPacketListenerImpl {
    private ServerGamePacketListenerImplMixin(MinecraftServer server, Connection connection, CommonListenerCookie cookie) {
        super(server, connection, cookie);
        throw new AssertionError();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void afterPlayBegins(MinecraftServer server, Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        ServerGamePacketListenerImpl that = (ServerGamePacketListenerImpl) (Object) this;
        ServerExt srv = FSB.serverGet(server);
        PlayerInfo info = new PlayerInfo(player.getUUID(), player.getName().getString());
        try {
            srv.session.newRemote(that, info);
        } catch (FSBArgumentException ignored) {

        }
    }

    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void beforeDisconnect(DisconnectionDetails details, CallbackInfo ci) {
        ServerGamePacketListenerImpl that = (ServerGamePacketListenerImpl) (Object) this;
        ServerExt srv = FSB.serverGet(server);
        srv.session.delRemote(that);
    }
}

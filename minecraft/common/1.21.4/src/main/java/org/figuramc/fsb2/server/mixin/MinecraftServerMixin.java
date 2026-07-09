package org.figuramc.fsb2.server.mixin;

import net.minecraft.server.MinecraftServer;
import org.figuramc.fsb2.server.FSBVersionSpecific;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method="runServer", at= @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;initServer()Z"))
    private void beforeSetupServer(CallbackInfo ci) {
        FSBVersionSpecific.startServer((MinecraftServer)(Object)this);
    }
}

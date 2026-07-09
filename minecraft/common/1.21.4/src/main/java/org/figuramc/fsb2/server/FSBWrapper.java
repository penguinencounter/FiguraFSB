package org.figuramc.fsb2.server;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.figuramc.fsb2.api.FSBConstants;
import org.jetbrains.annotations.NotNull;

public record FSBWrapper(ServerPacketImpl.Buf packagedBuffer) implements CustomPacketPayload {
    public static final Type<FSBWrapper> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FSBConstants.MOD_NAMESPACE, FSBConstants.FSB_PACKET_PATH));
    public static final StreamCodec<RegistryFriendlyByteBuf, FSBWrapper> CODEC = new PlainCodec();

    @Override
    public @NotNull Type<FSBWrapper> type() {
        return TYPE;
    }
}

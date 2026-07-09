package org.figuramc.fsb2.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class PlainCodec implements StreamCodec<RegistryFriendlyByteBuf, FSBWrapper> {
    @Override
    public @NotNull FSBWrapper decode(RegistryFriendlyByteBuf buf) {
        return new FSBWrapper(new ServerPacketImpl.Buf(Unpooled.copiedBuffer(buf)));
    }

    @Override
    public void encode(RegistryFriendlyByteBuf buf, FSBWrapper wrap) {
        ByteBuf actual = wrap.packagedBuffer().actual();
        buf.writeBytes(actual, actual.readerIndex(), actual.readableBytes());
    }
}

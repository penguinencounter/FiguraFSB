package org.figuramc.fsb2.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.figuramc.fsb2.api.FSBConstants;
import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;
import org.figuramc.fsb2.api.packets.Packet;
import org.jetbrains.annotations.NotNull;

public class ServerPacketImpl {
    public record Buf(ByteBuf actual) implements IFriendlyByteBuf {
        @Override
            public IFriendlyByteBuf writeByte(int b) {
                actual.writeByte(b);
                return this;
            }

            @Override
            public byte readByte() {
                return actual.readByte();
            }

            @Override
            public int readerIndex() {
                return actual.readerIndex();
            }

            @Override
            public IFriendlyByteBuf readerIndex(int i) {
                actual.readerIndex(i);
                return this;
            }

            @Override
            public int writerIndex() {
                return actual.writerIndex();
            }

            @Override
            public IFriendlyByteBuf writerIndex(int i) {
                actual.writerIndex(i);
                return this;
            }

            @Override
            public IFriendlyByteBuf setIndex(int reader, int writer) {
                actual.setIndex(reader, writer);
                return this;
            }

            @Override
            public int readableBytes() {
                return actual.readableBytes();
            }

            @Override
            public int writableBytes() {
                return actual.writableBytes();
            }

            @Override
            public int maxWritableBytes() {
                return actual.maxWritableBytes();
            }

            @Override
            public boolean isReadable() {
                return actual.isReadable();
            }

            @Override
            public boolean isReadable(int i) {
                return actual.isReadable(i);
            }

            @Override
            public boolean isWritable() {
                return actual.isWritable();
            }

            @Override
            public boolean isWritable(int i) {
                return actual.isWritable(i);
            }
        }

    public record FSBPayload(byte[] raw) implements CustomPacketPayload {
        public static final Type<FSBPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(FSBConstants.MOD_NAMESPACE, FSBConstants.FSB_PACKET_PATH));
        public static final StreamCodec<RegistryFriendlyByteBuf, FSBPayload> CODEC = StreamCodec.composite(ByteBufCodecs.BYTE_ARRAY, FSBPayload::raw, FSBPayload::new);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    public static void send(Connection connection, Packet<?> fPacket) {
        IFriendlyByteBuf buf = new Buf(Unpooled.buffer());
        fPacket.write(buf);
        String nameToUse = fPacket.identify().id.toString();
    }
}

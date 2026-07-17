package org.figuramc.fsb2.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.figuramc.fsb2.api.FSBConstants;
import org.figuramc.fsb2.api.packets.IFriendlyByteBuf;

public class ServerPacketImpl {
    public record Buf(FriendlyByteBuf actual) implements IFriendlyByteBuf {
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

    public static final ResourceLocation PACKET_ID = new ResourceLocation(FSBConstants.MOD_NAMESPACE, FSBConstants.FSB_PACKET_PATH);
}

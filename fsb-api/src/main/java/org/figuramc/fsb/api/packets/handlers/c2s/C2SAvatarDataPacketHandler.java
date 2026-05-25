package org.figuramc.fsb.api.packets.handlers.c2s;

import org.figuramc.fsb.api.FiguraServer;
import org.figuramc.fsb.api.FiguraUser;
import org.figuramc.fsb.api.packets.AvatarDataPacket;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;

public class C2SAvatarDataPacketHandler extends AuthorizedC2SPacketHandler<AvatarDataPacket> {
    public C2SAvatarDataPacketHandler(FiguraServer parent) {
        super(parent);
    }

    @Override
    protected void handle(FiguraUser sender, AvatarDataPacket packet) {
        parent.avatarManager().acceptAvatarChunk(sender, packet.streamId(), packet.avatarData(), packet.finalChunk());
    }

    @Override
    public AvatarDataPacket serialize(IFriendlyByteBuf byteBuf) {
        return new AvatarDataPacket(byteBuf);
    }
}

package org.figuramc.fsb.api.packets.handlers.c2s;

import org.figuramc.fsb.api.FiguraServer;
import org.figuramc.fsb.api.FiguraUser;
import org.figuramc.fsb.api.packets.c2s.C2SDeleteAvatarPacket;
import org.figuramc.fsb.api.packets.s2c.S2CAvatarDeletedPacket;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;

public class C2SDeleteAvatarPacketHandler extends AuthorizedC2SPacketHandler<C2SDeleteAvatarPacket> {
    public C2SDeleteAvatarPacketHandler(FiguraServer parent) {
        super(parent);
    }

    @Override
    protected void handle(FiguraUser sender, C2SDeleteAvatarPacket packet) {
        sender.removeOwnedAvatar(packet.avatarId());
        sender.removeEquippedAvatar();
        sender.sendPacket(new S2CAvatarDeletedPacket(packet.avatarId()));
    }

    @Override
    public C2SDeleteAvatarPacket serialize(IFriendlyByteBuf byteBuf) {
        return new C2SDeleteAvatarPacket(byteBuf);
    }
}

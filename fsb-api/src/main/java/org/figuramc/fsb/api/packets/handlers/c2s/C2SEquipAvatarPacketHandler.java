package org.figuramc.fsb.api.packets.handlers.c2s;

import org.figuramc.fsb.api.FiguraServer;
import org.figuramc.fsb.api.FiguraUser;
import org.figuramc.fsb.api.packets.c2s.C2SEquipAvatarsPacket;
import org.figuramc.fsb.api.packets.s2c.S2CNotifyPacket;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;

public class C2SEquipAvatarPacketHandler extends AuthorizedC2SPacketHandler<C2SEquipAvatarsPacket> {
    public C2SEquipAvatarPacketHandler(FiguraServer parent) {
        super(parent);
    }

    @Override
    protected void handle(FiguraUser sender, C2SEquipAvatarsPacket packet) {
        packet.avatars().forEach((id, pair) -> sender.setEquippedAvatar(id, pair.hash(), pair.ehash()));
        FiguraServer.getInstance().userManager().forEachUser(user -> {
            if (user != sender)
                user.sendPacket(new S2CNotifyPacket(sender.uuid()));
        });
    }

    @Override
    public C2SEquipAvatarsPacket serialize(IFriendlyByteBuf byteBuf) {
        return new C2SEquipAvatarsPacket(byteBuf);
    }
}

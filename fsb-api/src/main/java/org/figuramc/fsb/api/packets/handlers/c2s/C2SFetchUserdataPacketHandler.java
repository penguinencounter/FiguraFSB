package org.figuramc.fsb.api.packets.handlers.c2s;

import org.figuramc.fsb.api.FiguraServer;
import org.figuramc.fsb.api.FiguraUser;
import org.figuramc.fsb.api.FiguraUserManager;
import org.figuramc.fsb.api.packets.c2s.C2SFetchUserdataPacket;
import org.figuramc.fsb.api.packets.s2c.S2CUserdataNotFoundPacket;
import org.figuramc.fsb.api.packets.s2c.S2CUserdataPacket;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;

import java.util.BitSet;
import java.util.UUID;

public class C2SFetchUserdataPacketHandler extends AuthorizedC2SPacketHandler<C2SFetchUserdataPacket> {

    public C2SFetchUserdataPacketHandler(FiguraServer parent) {
        super(parent);
    }

    @Override
    protected void handle(FiguraUser sender, C2SFetchUserdataPacket packet) {
        FiguraUserManager manager = parent.userManager();
        UUID target = packet.target();
        if (manager.userExists(target)) {
            FiguraUser user = manager.getUser(packet.target());
            BitSet badges = user.prideBadges();
            sender.sendPacket(new S2CUserdataPacket(packet.transactionId(), target, badges, user.equippedAvatar()));
        }
        else {
            sender.sendPacket(new S2CUserdataNotFoundPacket(packet.transactionId()));
        }
    }

    @Override
    public C2SFetchUserdataPacket serialize(IFriendlyByteBuf byteBuf) {
        return new C2SFetchUserdataPacket(byteBuf);
    }
}

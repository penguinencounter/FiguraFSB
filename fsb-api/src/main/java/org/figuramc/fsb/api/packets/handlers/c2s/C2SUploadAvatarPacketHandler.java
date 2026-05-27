package org.figuramc.fsb.api.packets.handlers.c2s;

import org.figuramc.fsb.api.FiguraServer;
import org.figuramc.fsb.api.FiguraUser;
import org.figuramc.fsb.api.avatars.EHashPair;
import org.figuramc.fsb.api.packets.AllowIncomingStreamPacket;
import org.figuramc.fsb.api.packets.CloseIncomingStreamPacket;
import org.figuramc.fsb.api.packets.c2s.C2SUploadAvatarPacket;
import org.figuramc.fsb.api.packets.s2c.S2CAvatarReadyPacket;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;
import org.figuramc.fsb.api.utils.StatusCode;

public class C2SUploadAvatarPacketHandler extends AuthorizedC2SPacketHandler<C2SUploadAvatarPacket> {
    public C2SUploadAvatarPacketHandler(FiguraServer parent) {
        super(parent);
    }

    @Override
    protected void handle(FiguraUser sender, C2SUploadAvatarPacket packet) {
        boolean avatarExists = parent.avatarManager().avatarExists(packet.hash());
        if (getNewAvatarsCount(sender, packet.avatarId()) > parent.config().avatarsCountLimit(parent, sender.uuid())) {
            sender.sendPacket(new CloseIncomingStreamPacket(packet.streamId(), StatusCode.TOO_MANY_AVATARS));
        }
        if (avatarExists) {
            sender.replaceOrAddOwnedAvatar(packet.avatarId(), packet.hash(), packet.ehash());
            sender.sendPacket(new CloseIncomingStreamPacket(packet.streamId(), StatusCode.ALREADY_EXISTS));
            sender.sendPacket(new S2CAvatarReadyPacket(
                    packet.avatarId(),
                    new EHashPair(packet.hash(), packet.ehash())
            ));
        } else {
            parent.avatarManager()
                    .receiveAvatar(sender, packet.avatarId(), packet.streamId(), packet.hash(), packet.ehash());
            sender.sendPacket(new AllowIncomingStreamPacket(packet.streamId()));
        }
    }

    private int getNewAvatarsCount(FiguraUser user, String id) {
        return user.ownedAvatars().size() + (user.ownedAvatars().containsKey(id) ? 0 : 1);
    }

    @Override
    public C2SUploadAvatarPacket serialize(IFriendlyByteBuf byteBuf) {
        return new C2SUploadAvatarPacket(byteBuf);
    }
}

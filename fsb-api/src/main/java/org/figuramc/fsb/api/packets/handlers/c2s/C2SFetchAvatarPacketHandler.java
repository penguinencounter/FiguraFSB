package org.figuramc.fsb.api.packets.handlers.c2s;

import org.figuramc.fsb.api.FiguraServer;
import org.figuramc.fsb.api.FiguraUser;
import org.figuramc.fsb.api.events.Events;
import org.figuramc.fsb.api.events.avatars.AvatarFetchEvent;
import org.figuramc.fsb.api.packets.CloseOutcomingStreamPacket;
import org.figuramc.fsb.api.packets.c2s.C2SFetchAvatarPacket;
import org.figuramc.fsb.api.utils.Hash;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;
import org.figuramc.fsb.api.utils.StatusCode;

public class C2SFetchAvatarPacketHandler extends AuthorizedC2SPacketHandler<C2SFetchAvatarPacket> {
    public C2SFetchAvatarPacketHandler(FiguraServer parent) {
        super(parent);
    }

    @Override
    protected void handle(FiguraUser sender, C2SFetchAvatarPacket packet) {
        Hash hash = packet.hash();
        if (!parent.avatarManager().avatarExists(hash)) {
            sender.sendPacket(new CloseOutcomingStreamPacket(packet.streamId(), StatusCode.AVATAR_DOES_NOT_EXIST));
            return;
        }
        if (!Events.call(new AvatarFetchEvent(hash)).isCancelled()) {
            parent.avatarManager().sendAvatar(hash, sender.uuid(), packet.streamId());
        }
    }

    @Override
    public C2SFetchAvatarPacket serialize(IFriendlyByteBuf byteBuf) {
        return new C2SFetchAvatarPacket(byteBuf);
    }
}

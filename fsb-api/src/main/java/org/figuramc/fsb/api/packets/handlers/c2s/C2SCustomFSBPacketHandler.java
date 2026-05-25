package org.figuramc.fsb.api.packets.handlers.c2s;

import org.figuramc.fsb.api.FiguraServer;
import org.figuramc.fsb.api.FiguraUser;
import org.figuramc.fsb.api.packets.CustomFSBPacket;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;

public class C2SCustomFSBPacketHandler extends AuthorizedC2SPacketHandler<CustomFSBPacket> {

    public C2SCustomFSBPacketHandler(FiguraServer parent) {
        super(parent);
    }

    @Override
    protected void handle(FiguraUser sender, CustomFSBPacket packet) {
        parent.customPackets().handlePacket(sender, packet);
    }

    @Override
    public CustomFSBPacket serialize(IFriendlyByteBuf byteBuf) {
        return new CustomFSBPacket(byteBuf);
    }
}

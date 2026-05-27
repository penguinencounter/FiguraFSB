package org.figuramc.fsb.api.packets.handlers.c2s;

import org.figuramc.fsb.api.FiguraServer;
import org.figuramc.fsb.api.FiguraUser;
import org.figuramc.fsb.api.FiguraUserManager;
import org.figuramc.fsb.api.events.Events;
import org.figuramc.fsb.api.events.HandshakeEvent;
import org.figuramc.fsb.api.packets.c2s.C2SBackendHandshakePacket;
import org.figuramc.fsb.api.packets.s2c.S2CConnectedPacket;
import org.figuramc.fsb.api.packets.s2c.S2CRefusedPacket;
import org.figuramc.fsb.api.utils.IFriendlyByteBuf;

import java.util.UUID;

public class C2SHandshakeHandler implements C2SPacketHandler<C2SBackendHandshakePacket> {
    private final FiguraServer parent;

    public C2SHandshakeHandler(FiguraServer parent) {
        this.parent = parent;
    }

    @Override
    public C2SBackendHandshakePacket serialize(IFriendlyByteBuf byteBuf) {
        return new C2SBackendHandshakePacket();
    }

    @Override
    public void handle(UUID sender, C2SBackendHandshakePacket packet) {
        if (Events.call(new HandshakeEvent(sender)).isCancelled()) {
            parent.sendPacket(sender, new S2CRefusedPacket());
        } else {
            FiguraUserManager manager = parent.userManager();
            FiguraUser user = manager.setupOnlinePlayer(sender);
            user.sendPacket(parent.getHandshake(sender));
            manager.forEachUser(u -> {
                if (u != user) {
                    u.sendPacket(new S2CConnectedPacket(user.uuid()));
                }
            });
        }
    }
}

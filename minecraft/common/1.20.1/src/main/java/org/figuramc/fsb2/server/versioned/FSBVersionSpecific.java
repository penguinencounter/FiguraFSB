package org.figuramc.fsb2.server.versioned;

import net.minecraft.server.MinecraftServer;
import org.figuramc.fsb2.api.ProtocolSession;
import org.figuramc.fsb2.api.config.ServerIdentification;
import org.figuramc.fsb2.api.packets.s2c.S2CHelloPacket;
import org.figuramc.fsb2.server.BuiltInHandlers;
import org.figuramc.fsb2.server.FSB;
import org.figuramc.fsb2.server.ServerExt;
import org.figuramc.fsb2.server.internals.NetworkingService;

public class FSBVersionSpecific {
    public static void startServer(MinecraftServer minecraftServer) {
        ServerExt attachments = FSB.serverInit(minecraftServer);
        ProtocolSession session = attachments.session;
        BuiltInHandlers.setupTransferHandling(session);
    }

    private static long nTicks = 0L;

    public static void tick(MinecraftServer minecraftServer) {
        nTicks++;
        if (nTicks % 100 == 0) {
            VersionedNetworking networkSvc = (VersionedNetworking) NetworkingService.SERVICE;
            networkSvc.sendVia(
                    minecraftServer.getPlayerList()::broadcastAll,
                    new S2CHelloPacket(ServerIdentification.defaultValues())
            );
        }
    }
}

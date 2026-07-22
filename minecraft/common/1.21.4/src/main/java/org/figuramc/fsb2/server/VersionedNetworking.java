package org.figuramc.fsb2.server;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.figuramc.fsb2.services.FSBNetworkingService;

public interface VersionedNetworking extends FSBNetworkingService<ServerGamePacketListenerImpl> {
}

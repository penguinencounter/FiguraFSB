package org.figuramc.fsb.fabric;

import net.fabricmc.api.ModInitializer;
import org.figuramc.fsb2.FSB;

public class FSBFabricInit implements ModInitializer {
    @Override
    public void onInitialize() {
        FSB.LOGGER.info("FSB server running Fabric entrypoint");
        FSB.init();
    }
}

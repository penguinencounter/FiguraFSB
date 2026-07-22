package org.figuramc.fsb.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.figuramc.fsb2.server.FSB;
import org.figuramc.fsb2.server.FSBEnvType;

public class FSBFabricInit implements ModInitializer {
    @Override
    public void onInitialize() {
        EnvType environmentType = FabricLoader.getInstance().getEnvironmentType();
        FSB.LOGGER.info("FSB server running Fabric entrypoint {}", environmentType);
        FSB.init(environmentType == EnvType.CLIENT ? FSBEnvType.CLIENT : FSBEnvType.SERVER);
    }
}

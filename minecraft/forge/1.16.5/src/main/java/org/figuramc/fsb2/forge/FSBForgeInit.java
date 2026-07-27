package org.figuramc.fsb2.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.figuramc.fsb2.api.FSBConstants;
import org.figuramc.fsb2.server.FSB;
import org.figuramc.fsb2.server.FSBEnvType;

@Mod(FSBConstants.MOD_ID)
public class FSBForgeInit {
    public FSBForgeInit() {
        ModLoadingContext loader = ModLoadingContext.get();
        loader.registerExtensionPoint(
                ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true)
        );
        FSBEnvType environmentType = FMLEnvironment.dist == Dist.CLIENT ? FSBEnvType.CLIENT : FSBEnvType.SERVER;
        FSB.LOGGER.info("FSB server running Forge entrypoint {}", environmentType);
        FSB.init(environmentType);
    }
}

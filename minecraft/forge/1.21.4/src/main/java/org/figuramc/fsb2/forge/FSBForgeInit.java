package org.figuramc.fsb2.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.figuramc.fsb2.api.FSBConstants;
import org.figuramc.fsb2.server.FSB;
import org.figuramc.fsb2.server.FSBEnvType;

@Mod(FSBConstants.MOD_ID)
public class FSBForgeInit {
    public FSBForgeInit(ModLoadingContext loader) {
        loader.registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> IExtensionPoint.DisplayTest.IGNORESERVERONLY,
                        (a, b) -> true
                )
        );
        FSBEnvType environmentType = FMLEnvironment.dist == Dist.CLIENT ? FSBEnvType.CLIENT : FSBEnvType.SERVER;
        FSB.LOGGER.info("FSB server running Forge entrypoint {}", environmentType);
        FSB.init(environmentType);
    }
}

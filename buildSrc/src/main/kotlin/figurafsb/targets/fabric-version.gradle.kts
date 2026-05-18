/**
 * fabric, versioned
 * --------------
 *
 * Fabric loader & Minecraft
 */

package figurafsb.targets

import figurafsb.configurator.FSBPlatform
import figurafsb.configurator.OptionsExt
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor

plugins {
    id("figurafsb.minecraft")
}

the<OptionsExt>().apply {
    minecraft {
        platform = FSBPlatform.FABRIC

        plain(":common:any")
        plain(":fabric:any")
        // This basically includes fsb-api, but it does NOT count as JarInJar.
        plain(":fsb-api", noPrefix = true)
    }
}

the<OptionsExt>().then {
    val mc = it.minecraft!!
    architectury {
        platformSetupLoomIde()
        fabric()
    }

    val version = versionFor(mc.minecraftVersion)

    val upstreamConfigurations: MutableMap<String, String> by extra
    val plainConfigurations: MutableMap<String, String> by extra

    configurations {
        afterEvaluate { // wait for loom
            named("developmentFabric") {
                extendsFrom(
                    *(upstreamConfigurations + plainConfigurations).values.map { named(it) }.toTypedArray()
                )
            }
        }
    }

    dependencies {
        version.dependencyContext { d ->
            modApi(d.fabricApi())
            modImplementation(d.fabricLoader())
        }
    }
}

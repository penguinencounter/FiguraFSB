/**
 * forge, versioned
 * --------------
 *
 * Forge loader & api & Minecraft
 */

package figurafsb.targets

import figurafsb.configurator.FSBPlatform
import figurafsb.configurator.OptionsExt
import figurafsb.versioning.FSBDependencyContext
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor

plugins {
    id("figurafsb.minecraft")
}

the<OptionsExt>().apply {
    minecraft {
        platform = FSBPlatform.FORGE

        plain(":common:any")
        plain(":forge:any")
        plain(":fsb-api", noPrefix = true)
    }
}

the<OptionsExt>().then {
    val mc = it.minecraft!!
    architectury {
        platformSetupLoomIde()
        forge()
    }

    loom {
        forge {}
    }

    val version = versionFor(mc.minecraftVersion)

    val upstreamConfigurations: MutableMap<String, String> by extra
    val plainConfigurations: MutableMap<String, String> by extra

    configurations {
        afterEvaluate { // wait for loom
            named("developmentForge") {
                extendsFrom(
                    *(upstreamConfigurations + plainConfigurations).values.map { named(it) }.toTypedArray()
                )
            }
        }
    }

    dependencies {
        version.dependencyContext { d ->
            "forge"(d.forgeLoader()) // it doesn't resolve.
        }
    }
}

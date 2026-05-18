package figurafsb.targets

import figurafsb.configurator.OptionsExt
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor

plugins {
    id("figurafsb.minecraft")
}

the<OptionsExt>().apply {
    minecraft {
        plain(":common:any")
        plain(":fsb-api", noPrefix = true)
    }
}

the<OptionsExt>().then {
    val mc = it.minecraft!!
    architectury {
        common("fabric", "forge", "neoforge")
    }

    val version = versionFor(mc.minecraftVersion)

    dependencies {
        version.dependencyContext { d ->
        }
    }
}
package figurafsb.targets

import figurafsb.configurator.OptionsExt
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor

plugins {
    id("figurafsb.minecraft")
}

the<OptionsExt>().then {
    val mc = it.minecraft!!
    architectury {
        fabric {}
    }

    val version = versionFor(mc.minecraftVersion)

    dependencies {
        version.dependencyContext { d ->
            modApi(d.fabricApi())
            modImplementation(d.fabricLoader())
        }
    }
}

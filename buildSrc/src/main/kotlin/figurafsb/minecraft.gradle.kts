package figurafsb

import figurafsb.configurator.OptionsExt
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor

plugins {
    id("figurafsb.base")
    id("architectury-plugin")
    id("dev.architectury.loom")
}

the<OptionsExt>().then {
    val mcData = it.minecraft ?: throw IllegalStateException("this target requires Minecraft versioning data, but none is present in the fsbOptions block")
    val version = versionFor(mcData.minecraftVersion)

    dependencies {
        version.dependencyContext { d ->
            minecraft(d.minecraft())

            mappings(loom.layered {
                officialMojangMappings()
                parchment(d.parchment())
            })
        }
    }
}

import figurafsb.versioning.VERSIONS
import figurafsb.versioning.FSBDeps

plugins {
    id("figurafsb.minecraft")
}

evaluationDependsOn(":")

cloche {
    metadata {
        modId = "figurafsb"
        name = "Figura Server Backend"
        description = "Figura's server backend components"

        author("Lexize")
        author("PenguinEncounter")
    }

    // Gradle dies if this isn't here for some reason
    common {}

    for (version in VERSIONS.values) {
        val mc = version.minecraft
        val fabri = version.fabric
        val forg = version.forge

        val thisVersionCommon = common("$mc:common") {}

        if (fabri != null) fabric("$mc:fabric") {
            minecraftVersion = mc
            loaderVersion = fabri.loader

            dependencies {
                modLocalRuntime(FSBDeps.fabricApi(fabri.api))
            }

            dependsOn(thisVersionCommon)
        }
        if (forg != null) forge("$mc:forge") {
            minecraftVersion = mc
            loaderVersion = forg.loader

            dependsOn(thisVersionCommon)
        }
    }
}
import figurafsb.versioning.VERSIONS
import figurafsb.versioning.FSBDeps
import net.msrandom.stubs.GenerateStubApi

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
        val neoforg = version.neoforge

        val thisVersionCommon = common("common:$mc") {}

        if (fabri != null) fabric("fabric:$mc") {
            minecraftVersion = mc
            loaderVersion = fabri.loader

            dependencies {
                modLocalRuntime(FSBDeps.fabricApi(fabri.api))
            }

            // TODO: cloche bug
//            includedClient()

            runs {
                server()
                client()
            }

            dependsOn(thisVersionCommon)
        }
        if (forg != null) forge("forge:$mc") {
            minecraftVersion = mc
            loaderVersion = forg.loader

            runs {
                server()
                client()
            }

            dependsOn(thisVersionCommon)
        }
        if (neoforg != null) neoforge("neoforge:$mc") {
            minecraftVersion = mc
            loaderVersion = neoforg.loader

            runs {
                server()
                client()
            }

            dependsOn(thisVersionCommon)
        }
    }

    // fix 1.20.6+ forge
    configurations.configureEach {
        resolutionStrategy.capabilitiesResolution.withCapability("cpw.mods:modlauncher") {
            selectHighestVersion()
        }
    }
}

tasks.named<GenerateStubApi>("createCommonApiStub") {
    excludes.add("net.java.dev.jna")
}

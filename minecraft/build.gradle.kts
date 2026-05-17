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

    val anyAny = common {
        dependencies {
            implementation(project(":fsb-api"))
        }
    }

    val fabricAny = common("fabric:any") {
        dependsOn(anyAny)
    }
    val forgeAny = common("forge:any") {
        dependsOn(anyAny)
    }
    val neoForgeAny = common("neoforge:any") {
        dependsOn(anyAny)
    }

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
                implementation(project(":fsb-api"))
                include(project(":fsb-api"))
            }

            includedClient()

            runs {
                server()
                client()
            }

            dependsOn(thisVersionCommon, fabricAny)
        }
        if (forg != null) forge("forge:$mc") {
            minecraftVersion = mc
            loaderVersion = forg.loader

            runs {
                server()
                client()
            }

            dependsOn(thisVersionCommon, forgeAny)
        }
        if (neoforg != null) neoforge("neoforge:$mc") {
            minecraftVersion = mc
            loaderVersion = neoforg.loader

            runs {
                server()
                client()
            }

            dependsOn(thisVersionCommon, neoForgeAny)
        }
    }

    // fix 1.20.6+ forge
    configurations.configureEach {
        resolutionStrategy.capabilitiesResolution.withCapability("cpw.mods:modlauncher") {
            selectHighestVersion()
        }
    }
}

// cloche doesn't get the message earlier so...
lateinit var api: ProjectDependency
dependencies {
    api = project(":fsb-api")
    implementation(api)
}

configurations.configureEach {
    if (this.name.matches(Regex("^common\\d+CompileClasspath$")))
        dependencies.addLater(provider { api })
}

tasks.named<GenerateStubApi>("createCommonApiStub") {
    excludes.add("net.java.dev.jna")
}

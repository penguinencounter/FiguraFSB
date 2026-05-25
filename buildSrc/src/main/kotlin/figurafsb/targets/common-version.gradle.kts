package figurafsb.targets

import figurafsb.configurator.OptionsExt
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor
import net.fabricmc.loom.task.RemapJarTask

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

    project.version = "${rootProject.version}+${mc.minecraftVersion}"
    project.group = rootProject.group


    val artifactRoot: String by project

    val mojmapJar = tasks.register<RemapJarTask>("mojmapJar") {
        val remapJar = tasks.named<RemapJarTask>("remapJar")
        dependsOn(remapJar)
        inputFile = remapJar.get().archiveFile
        sourceNamespace = "intermediary"
        targetNamespace = "named"
        archiveClassifier = "mojmapped"
    }

    publishing {
        publications {
            register("maven", MavenPublication::class) {
                artifactId = "${artifactRoot}-common-intermediary"

                val remapJar = tasks.named<RemapJarTask>("remapJar")
                artifact(remapJar) {
                    builtBy(remapJar)
                    classifier = ""
                }

                val sourcesJar = tasks.named<Jar>("sourcesJar")
                artifact(sourcesJar) {
                    builtBy(sourcesJar)
                    classifier = "sources"
                }
            }
            register("mojmap", MavenPublication::class) {
                artifactId = "${artifactRoot}-common-mojmap"

                artifact(mojmapJar) {
                    builtBy(mojmapJar)
                    classifier = ""
                }

                val sourcesJar = tasks.named<Jar>("sourcesJar")
                artifact(sourcesJar) {
                    builtBy(sourcesJar)
                    classifier = "sources"
                }
            }
        }
    }
}
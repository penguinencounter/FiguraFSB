/**
 * neoforge, versioned
 * --------------
 *
 * NeoForge loader & api & Minecraft
 */

package figurafsb.targets

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import figurafsb.configurator.FSBPlatform
import figurafsb.configurator.OptionsExt
import figurafsb.versioning.FSBDependencyContext
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor
import org.gradle.kotlin.dsl.named

plugins {
    id("figurafsb.minecraft")
}

the<OptionsExt>().apply {
    minecraft {
        platform = FSBPlatform.NEOFORGE

        plain(":common:any")
//        plain(":forge:any")
        plain(":fsb-api", noPrefix = true)
    }
}

the<OptionsExt>().then {
    val mc = it.minecraft!!
    architectury {
        platformSetupLoomIde()
        neoForge()
    }

    loom {
        neoForge {}
    }

    val version = versionFor(mc.minecraftVersion)

    val upstreamConfigurations: MutableMap<String, String> by extra
    val plainConfigurations: MutableMap<String, String> by extra

    configurations {
        afterEvaluate { // wait for loom
            named("developmentNeoForge") {
                extendsFrom(
                    *(upstreamConfigurations + plainConfigurations).values.map { named(it) }.toTypedArray()
                )
            }
        }
    }

    dependencies {
        version.dependencyContext { d ->
            "neoForge"(d.neoForgeLoader())
        }
    }

    project.version = "${rootProject.version}+${mc.minecraftVersion}-neoforge"
    project.group = rootProject.group
    val artifactRoot: String by project

    publishing {
        publications {
            register("maven", MavenPublication::class) {
                artifactId = "${artifactRoot}-neoforge"

                val shadowJar = tasks.named<ShadowJar>("shadowJar")
                artifact(shadowJar) {
                    builtBy(shadowJar)
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

/**
 * forge, versioned
 * --------------
 *
 * Forge loader & api & Minecraft
 */

package figurafsb.targets

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import figurafsb.configurator.FSBPlatform
import figurafsb.configurator.OptionsExt
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor
import libs

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
}.adapt {
    val opt = reify()
    val ver = opt.minecraft?.minecraftVersion
        ?: throw IllegalStateException("need Minecraft configuration on forge-version")
    if (ver != "1.16.5") this.minecraft {
        plain(":common:modernish")
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
            "forge"(d.forgeLoader()) // it doesn't resolve. sorry :/

            libs.mixinExtras.forge.let { dep ->
                include(dep)
                implementation(dep)
            }
            libs.mixinExtras.common.let { dep ->
                compileOnly(dep)
                annotationProcessor(dep)
            }
        }
    }

    project.version = "${rootProject.version}+${mc.minecraftVersion}-forge"
    project.group = rootProject.group
    val artifactRoot: String by project

    publishing {
        publications {
            register("maven", MavenPublication::class) {
                artifactId = "${artifactRoot}-forge"

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

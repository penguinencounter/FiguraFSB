/**
 * fabric, versioned
 * --------------
 *
 * Fabric loader & Minecraft
 */

package figurafsb.targets

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import figurafsb.configurator.FSBPlatform
import figurafsb.configurator.OptionsExt
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

plugins {
    id("figurafsb.minecraft")
}

the<OptionsExt>().apply {
    minecraft {
        platform = FSBPlatform.FABRIC

        plain(":common:any")
        plain(":fabric:any")
        // This basically includes fsb-api, but it does NOT count as JarInJar.
        plain(":fsb-api", noPrefix = true)
    }
}.adapt {
    val opt = reify()
    val ver = opt.minecraft?.minecraftVersion ?: throw IllegalStateException("need Minecraft configuration on fabric-version")
    if (ver != "1.16.5") this.minecraft {
        plain(":common:modernish")
    }
}

the<OptionsExt>().then {
    val mc = it.minecraft!!
    architectury {
        platformSetupLoomIde()
        fabric()
    }

    val version = versionFor(mc.minecraftVersion)

    val upstreamConfigurations: MutableMap<String, String> by extra
    val plainConfigurations: MutableMap<String, String> by extra

    configurations {
        afterEvaluate { // wait for loom
            named("developmentFabric") {
                extendsFrom(
                    *(upstreamConfigurations + plainConfigurations).values.map { named(it) }.toTypedArray()
                )
            }
        }

        version.dependencyContext { d ->
            if (d.fabricForced()) configureEach {
                resolutionStrategy.force(d.fabricLoader())
            }
        }
    }

    dependencies {
        version.dependencyContext { d ->
            modApi(d.fabricApi())
            modImplementation(d.fabricLoader())
        }
    }

    project.version = "${rootProject.version}+${mc.minecraftVersion}-fabric"
    project.group = rootProject.group
    val artifactRoot: String by project

    publishing {
        publications {
            register("maven", MavenPublication::class) {
                artifactId = "${artifactRoot}-fabric"

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

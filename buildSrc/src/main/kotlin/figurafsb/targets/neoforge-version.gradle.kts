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
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor
import figurafsb.yesno
import libs
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.named

plugins {
    id("figurafsb.minecraft")
}

the<OptionsExt>().apply {
    minecraft {
        platform = FSBPlatform.NEOFORGE

        plain(":common:any")
        plain(":neoforge:any")
        plain(":fsb-api", noPrefix = true)
    }
}.adapt {
    val opt = reify()
    val ver = opt.minecraft?.minecraftVersion
        ?: throw IllegalStateException("need Minecraft configuration on neoforge-version")
    if (ver != "1.16.5") this.minecraft {
        plain(":common:modernish")
    }
}

val snapshot: String? by project

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

            libs.mixinExtras.neoforge.let { dep ->
                implementation(dep)
                include(dep)  // i think??
            }
        }
    }

    project.version = buildString {
        append("${rootProject.version}+${mc.minecraftVersion}-neoforge")
        if (yesno(snapshot)) append("-SNAPSHOT")
    }
    project.group = rootProject.group
    val artifactRoot: String by project

    val remapComponentJar by tasks.registering(RemapJarTask::class) {
        description = "remap the classes in this project only (non-shadow)"

        dependsOn(tasks.jar)
        inputFile = tasks.jar.get().archiveFile
        archiveClassifier = "component"
    }

    publishing {
        publications {
            register("maven", MavenPublication::class) {
                artifactId = "${artifactRoot}-neoforge"

                val remapJar = tasks.named<RemapJarTask>("remapJar")
                artifact(remapJar) {
                    builtBy(remapJar)
                    classifier = ""
                }

                artifact(remapComponentJar) {
                    builtBy(remapComponentJar)
                    classifier = "component"
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

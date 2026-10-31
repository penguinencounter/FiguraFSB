/**
 * neoforge, versioned
 * --------------
 *
 * NeoForge loader & api & Minecraft
 */

package figurafsb.targets

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import figurafsb.Names.Companion.names
import figurafsb.asNullable
import figurafsb.configurator.FSBPlatform
import figurafsb.configurator.OptionsExt
import figurafsb.proc.Templater
import figurafsb.proc.shadowDefaults
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor
import libs
import net.fabricmc.loom.task.RemapJarTask

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

fun String.property() = providers.gradleProperty(this)

val snapshot: String? by "snapshot".property().asNullable

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

    val template = extra["template"] as Templater
    @Suppress("UNCHECKED_CAST") val upstreamConfigurations =
        extra["upstreamConfigurations"] as MutableMap<String, String>
    @Suppress("UNCHECKED_CAST") val plainConfigurations =
        extra["plainConfigurations"] as MutableMap<String, String>

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

    val names = project.names()
    project.version = names.version(mc.minecraftVersion)
    project.group = names.neoforgeGroup

    val componentShadowJar = tasks.register<ShadowJar>("componentShadowJar") {
        shadowDefaults(template)
        exclude("architectury.common.json")

        description = "this + neoforge-any"
        from(sourceSets.main.map { i -> i.output })
        configurations.add(project.configurations.named("upstreamNeoforgeAny"))
        configurations.add(project.configurations.named("includedResources"))
        archiveClassifier = "component-shadow"
    }

    val remapComponentJar = tasks.register<RemapJarTask>("remapComponentJar") {
        description = "remap the classes in this project only (non-shadow)"

        dependsOn(componentShadowJar)
        inputFile = componentShadowJar.get().archiveFile
        archiveClassifier = "component"
    }

    publishing {
        publications {
            register("maven", MavenPublication::class) {
                artifactId = "${names.artifactRoot}-neoforge"

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

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
import figurafsb.proc.Templater
import figurafsb.proc.shadowDefaults
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor
import figurafsb.yesno
import libs
import net.fabricmc.loom.task.RemapJarTask

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

val snapshot: String? by project

the<OptionsExt>().then {
    val mc = it.minecraft!!
    architectury {
        platformSetupLoomIde()
        forge()
    }

    loom {
        forge {
            mixinConfig("figura-fsb.mixins.json")
        }
    }

    val version = versionFor(mc.minecraftVersion)

    val template: Templater by extra
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

    project.version = buildString {
        append("${rootProject.version}+${mc.minecraftVersion}-forge")
        if (yesno(snapshot)) append("-SNAPSHOT")
    }
    project.group = rootProject.group
    val artifactRoot: String by project

    val componentShadowJar by tasks.registering(ShadowJar::class) {
        shadowDefaults(template)
        description = "this + forge-any"
        configurations.add(project.configurations.named("upstreamForgeAny"))
        configurations.add(project.configurations.named("includedResources"))
        archiveClassifier = "component-shadow"
    }

    val remapComponentJar by tasks.registering(RemapJarTask::class) {
        description = "remap the classes in this project only (non-shadow)"

        dependsOn(componentShadowJar)
        inputFile = componentShadowJar.get().archiveFile
        archiveClassifier = "component"
    }

    publishing {
        publications {
            register("maven", MavenPublication::class) {
                artifactId = "${artifactRoot}-forge"

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

package figurafsb.targets

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import figurafsb.configurator.OptionsExt
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor
import figurafsb.yesno
import libs
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.provideDelegate

plugins {
    id("figurafsb.minecraft")
}

the<OptionsExt>().apply {
    minecraft {
        plain(":common:any")
        plain(":fsb-api", noPrefix = true)
    }
}.adapt {
    val opt = reify()
    val ver = opt.minecraft?.minecraftVersion ?: throw IllegalStateException("need Minecraft configuration on common-version")
    if (ver != "1.16.5") this.minecraft {
        plain(":common:modernish")
    }
}

val snapshot: String? by project

the<OptionsExt>().then {
    val mc = it.minecraft!!
    architectury {
        common("fabric", "forge", "neoforge")
    }

    val version = versionFor(mc.minecraftVersion)

    dependencies {
        version.dependencyContext { d ->
            libs.mixinExtras.common.let { dep ->
                annotationProcessor(dep)
                compileOnly(dep)
            }
            compileOnly(d.mixinCommon())
        }
    }

    project.version = buildString {
        append("${rootProject.version}+${mc.minecraftVersion}")
        if (yesno(snapshot)) append("-SNAPSHOT")
    }
    project.group = rootProject.group


    val artifactRoot: String by project

    val shadowJar = tasks.named<ShadowJar>("shadowJar")
    val componentJar = tasks.named<Jar>("jar")

    val remapJar = tasks.named<RemapJarTask>("remapJar") {
        dependsOn(shadowJar)
        inputFile = shadowJar.get().archiveFile
        archiveClassifier = "fat"
    }

    val remapComponentJar by tasks.registering(RemapJarTask::class) {
        description = "remap the classes in this project only (non-shadow)"

        dependsOn(componentJar)
        inputFile = componentJar.get().archiveFile
        archiveClassifier = null
    }

    val mojmapJar by tasks.registering(RemapJarTask::class) {
        description = "remap the classes in the fatjar to mojmap"

        dependsOn(remapJar)
        inputFile = remapJar.get().archiveFile
        sourceNamespace = "intermediary"
        targetNamespace = "named"
        archiveClassifier = "fat-mojmap"
    }

    val mojmapComponentJar by tasks.registering(RemapJarTask::class) {
        description = "remap the classes in this project only to mojmap"

        dependsOn(remapComponentJar)
        inputFile = remapComponentJar.get().archiveFile
        sourceNamespace = "intermediary"
        targetNamespace = "named"
        archiveClassifier = "mojmap"
    }

    publishing {
        publications {
            register("maven", MavenPublication::class) {
                artifactId = "${artifactRoot}-common-intermediary"

                artifact(componentJar) {
                    builtBy(componentJar)
                    classifier = ""
                }

                artifact(remapJar) {
                    builtBy(remapJar)
                    classifier = "fat"
                }

                val sourcesJar = tasks.named<Jar>("sourcesJar")
                artifact(sourcesJar) {
                    builtBy(sourcesJar)
                    classifier = "sources"
                }
            }
            register("mojmap", MavenPublication::class) {
                artifactId = "${artifactRoot}-common-mojmap"

                artifact(mojmapComponentJar) {
                    builtBy(mojmapComponentJar)
                    classifier = ""
                }

                artifact(mojmapJar) {
                    builtBy(mojmapJar)
                    classifier = "fat"
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
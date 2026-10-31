package figurafsb.targets

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import figurafsb.Names.Companion.names
import figurafsb.asNullable
import figurafsb.configurator.OptionsExt
import figurafsb.proc.Templater
import figurafsb.proc.enableRemap
import figurafsb.proc.shadowDefaults
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor
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

fun String.property() = providers.gradleProperty(this)

val snapshot: String? by "snapshot".property().asNullable

the<OptionsExt>().then {
    val mc = it.minecraft!!
    architectury {
        common("fabric", "forge", "neoforge")
    }

    val version = versionFor(mc.minecraftVersion)
    val template = extra["template"] as Templater

    dependencies {
        version.dependencyContext { d ->
            libs.mixinExtras.common.let { dep ->
                annotationProcessor(dep)
                compileOnly(dep)
            }
            compileOnly(d.mixinCommon())
        }
    }

    val names = project.names()
    project.version = names.version(mc.minecraftVersion)
    project.group = names.commonGroup

    val shadowJar = tasks.named<ShadowJar>("shadowJar")

    val remapJar = tasks.named<RemapJarTask>("remapJar") {
        dependsOn(shadowJar)
        enableRemap("intermediary")

        inputFile = shadowJar.get().archiveFile
        archiveClassifier = "fat"
    }

   val componentShadowJar = tasks.register<ShadowJar>("componentShadowJar") {
        shadowDefaults(template)
        description = "this + modernish, if applicable"
        from(sourceSets.main.map {i -> i.output})
        if (version.minecraft != "1.16.5")
            configurations.add(project.configurations.named("upstreamCommonModernish"))
        archiveClassifier = "component-shadow"
    }

    val componentJarTarget = if (version.minecraft != "1.16.5") componentShadowJar else tasks.named<Jar>("jar")

   val remapComponentJar = tasks.register<RemapJarTask>("remapComponentJar") {
        description = "remap the classes in this project only (non-shadow)"
        dependsOn(componentJarTarget)
        enableRemap("intermediary")

        inputFile = componentJarTarget.get().archiveFile
        archiveClassifier = null
    }

   val mojmapJar = tasks.register<RemapJarTask>("mojmapJar") {
        description = "remap the classes in the fatjar to mojmap"

        dependsOn(remapJar)
        enableRemap("named")

        inputFile = remapJar.get().archiveFile
        sourceNamespace = "intermediary"
        targetNamespace = "named"
        archiveClassifier = "fat-mojmap"
    }

   val mojmapComponentJar = tasks.register<RemapJarTask>("mojmapComponentJar") {
        description = "remap the classes in this project only to mojmap"

        dependsOn(remapComponentJar)
        enableRemap("named")

        inputFile = remapComponentJar.get().archiveFile
        sourceNamespace = "intermediary"
        targetNamespace = "named"
        archiveClassifier = "mojmap"
    }

    publishing {
        publications {
            register("maven", MavenPublication::class) {
                artifactId = "${names.artifactRoot}-common-intermediary"

                artifact(remapComponentJar) {
                    builtBy(remapComponentJar)
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
                artifactId = "${names.artifactRoot}-common-mojmap"

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
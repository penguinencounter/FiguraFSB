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
import figurafsb.proc.Templater
import figurafsb.proc.shadowDefaults
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor
import figurafsb.yesno
import libs
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.invoke
import kotlin.collections.plus

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
    val ver = opt.minecraft?.minecraftVersion
        ?: throw IllegalStateException("need Minecraft configuration on fabric-version")
    if (ver != "1.16.5") this.minecraft {
        plain(":common:modernish")
    }
}

val snapshot: String? by project

the<OptionsExt>().then {
    val mc = it.minecraft!!
    architectury {
        platformSetupLoomIde()
        fabric()
    }

    val version = versionFor(mc.minecraftVersion)

    val upstreamConfigurations: MutableMap<String, String> by extra
    val plainConfigurations: MutableMap<String, String> by extra
    val template: Templater by extra

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
            libs.mixinExtras.fabric.let { dep ->
                include(dep)
                implementation(dep)
                annotationProcessor(dep)
            }
        }
    }

    project.version = buildString {
        append("${rootProject.version}+${mc.minecraftVersion}-fabric")
        if (yesno(snapshot)) append("-SNAPSHOT")
    }
    project.group = rootProject.group
    val artifactRoot: String by project

    val componentShadowJar by tasks.registering(ShadowJar::class) {
        shadowDefaults(template)
        description = "this + fabric-any"
        from(sourceSets.main.map {i -> i.output})
        configurations.add(project.configurations.named("upstreamFabricAny"))
        configurations.add(project.configurations.named("includedResources"))
        archiveClassifier = "component-shadow"
    }

    val remapComponentJar by tasks.registering(RemapJarTask::class) {
        description = "remap the classes in this project only? + fabric-any"

        dependsOn(componentShadowJar)
        inputFile = componentShadowJar.get().archiveFile
        archiveClassifier = "component"
    }

    publishing {
        publications {
            register("maven", MavenPublication::class) {
                artifactId = "${artifactRoot}-fabric"

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

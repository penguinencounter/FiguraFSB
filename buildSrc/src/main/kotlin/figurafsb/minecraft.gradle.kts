package figurafsb

import figurafsb.configurator.OptionsExt
import figurafsb.proc.JSONMerger
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor
import org.gradle.api.attributes.LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE

plugins {
    id("figurafsb.base")
    id("architectury-plugin")
    id("dev.architectury.loom")
    id("com.gradleup.shadow")
}

the<OptionsExt>().then {
    val mcData = it.minecraft
        ?: throw IllegalStateException("this target requires Minecraft versioning data, but none is present in the fsbOptions block")
    val version = versionFor(mcData.minecraftVersion)
    val plainConfigurations: MutableMap<String, String> by extra { mutableMapOf() }
    val upstreamConfigurations: MutableMap<String, String> by extra { mutableMapOf() }
    val upstreamShadows: MutableMap<String, String> by extra { mutableMapOf() }

    val resourceIncludes by configurations.dependencyScope("resourceIncludes")
    val includedResources by configurations.resolvable("includedResources") {
        extendsFrom(resourceIncludes)

        attributes {
            // match with base.gradle.kts
            attribute(LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("resource-jar"))
        }
    }

    configurations {
        for (upstream in mcData.upstreams) {
            if (mcData.platform == null) throw IllegalStateException(
                """
                    Can't do Loom upstreams on a non-loader project, sorry! (configuring ${project.path})
                    (we don't know at what configuration to use for shadowJar-ing unless you tell us)
                    - If this is actually a loader project, 'platform' needs to be set in fsbOptions.minecraft.
                    - If this dependency isn't Loom, use 'plain' instead of 'upstream'.
                """.trimIndent()
            )

            val name = projToConfName(upstream.removeMinecraftPrefix(), listOf("upstream"))
            upstreamConfigurations[upstream] = name
            register(name)
            val shadow = projToConfName(upstream.removeMinecraftPrefix(), listOf("upstream", "shadow"))
            upstreamShadows[upstream] = shadow
            register(shadow)
        }
        for (upstream in mcData.plainUpstreams) {
            val name = projToConfName(upstream.removeMinecraftPrefix(), listOf("upstream"))
            plainConfigurations[upstream] = name
            register(name)
        }
        val allConfigurations =
            plainConfigurations.values.map { named(it) } + upstreamConfigurations.values.map { named(it) }
        compileClasspath {
            extendsFrom(*allConfigurations.toTypedArray())
        }
        runtimeClasspath {
            extendsFrom(*allConfigurations.toTypedArray())
        }
    }

    dependencies {
        version.dependencyContext { d ->
            minecraft(d.minecraft())

            mappings(loom.layered {
                officialMojangMappings()
                parchment(d.parchment())
            })
        }

        for ((projName, confName) in plainConfigurations) {
            add(confName, project(projName)) { isTransitive = false }
            resourceIncludes(project(projName))
        }
        for ((projName, confName) in upstreamConfigurations) {
            add(confName, project(projName, "namedElements")) { isTransitive = false }
            resourceIncludes(project(projName))
        }
        mcData.platform?.let { platform ->
            for ((projName, confName) in upstreamShadows) {
                add(confName, project(projName, "transformProduction${platform.capitalized}")) { isTransitive = false }
            }
        }
    }

    tasks {
        shadowJar {
            duplicatesStrategy = DuplicatesStrategy.WARN
            exclude("architectury.common.json")

            mergeServiceFiles()
            filesMatching("META-INF/services/**") {
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }

            transform(JSONMerger())
            filesMatching("**/*.json") {
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }

            configurations.addAll(provider {
                (upstreamShadows + plainConfigurations).values.map { project.configurations.get(it) }
            })
            configurations.add(includedResources)

            archiveClassifier = "dev-shadow"
        }

        remapJar {
            dependsOn(shadowJar)
            inputFile = shadowJar.get().archiveFile
            archiveClassifier = null
        }

        jar {
            archiveClassifier = "dev"
        }
    }
}

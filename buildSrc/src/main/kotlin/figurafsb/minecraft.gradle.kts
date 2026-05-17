package figurafsb

import figurafsb.configurator.OptionsExt
import figurafsb.versioning.dependencyContext
import figurafsb.versioning.versionFor

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

    configurations {
        for (upstream in mcData.upstreams) {
            evaluationDependsOn(upstream)
            val name = projToConfName(upstream.removeMinecraftPrefix(), listOf("upstream"))
            upstreamConfigurations[upstream] = name
            register(name)
            val shadow = projToConfName(upstream.removeMinecraftPrefix(), listOf("upstream", "shadow"))
            upstreamShadows[upstream] = shadow
            register(shadow)
        }
        for (upstream in mcData.plainUpstreams) {
            evaluationDependsOn(upstream)
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
        }
        for ((projName, confName) in upstreamConfigurations) {
            add(confName, project(projName, "namedElements")) { isTransitive = false }
        }
    }

    sourceSets {
        val allResources = (plainConfigurations + upstreamConfigurations).keys.map {
            project(it).sourceSets.main.get().resources
        }
        main {
            resources {
                allResources.forEach(::source)
            }
        }
    }

    tasks {
        shadowJar {
            exclude("architectury.common.json")
            configurations.addAll(provider {
                (upstreamShadows + plainConfigurations).values.map { project.configurations.get(it) }
            })
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

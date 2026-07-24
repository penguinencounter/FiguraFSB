package figurafsb

import figurafsb.configurator.OptionsExt
import figurafsb.proc.Templater
import figurafsb.versioning.addToTemplate
import figurafsb.versioning.versionFor
import gradle.kotlin.dsl.accessors._428a6a25e01afd3767e10657b288cdc3.processResources
import libs
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.attributes.LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE

plugins {
    java
    `java-library`
    `maven-publish`
    idea
    id("figurafsb.configurator.ext")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.neoforged.net/releases") }
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://maven.minecraftforge.net/") }
    maven { url = uri("https://maven.architectury.dev/") }
    maven { url = uri("https://libraries.minecraft.net/") }
}

// move resources around
val resourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("resources")
    from(tasks.processResources)
}

configurations {
    consumable("resourceJars") {
        attributes {
            attribute(LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("resource-jar"))
        }
    }
}

artifacts {
    add("resourceJars", resourceJar)
}

dependencies {
    compileOnly(libs.annotations)
}

val modVersion: String by project
val overrideMC: String? by project

the<OptionsExt>().then {

    val repl = mutableMapOf<String, String?>(
        "modVersion" to modVersion,
    )
    val minecraftVersion = it.minecraft?.minecraftVersion ?: overrideMC
    minecraftVersion?.let { raw ->
        val version = versionFor(raw)
        version.addToTemplate(repl, "mc")
    }

    val template = Templater(repl)

    java {
        withSourcesJar()

        toolchain {
            languageVersion = JavaLanguageVersion.of(it.javaToolchain.actual)
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release = it.javaVersion
    }

    tasks.withType<ProcessResources>().configureEach {
        inputs.property("replacements", template)

        filesMatching("**/*.json") {
            filter<ReplaceTokens>(*template.toReplaceTokensOptions())
        }
        filesMatching("**/*.toml") {
            filter<ReplaceTokens>(*template.toReplaceTokensOptions())
        }
    }
}

publishing {
    repositories {
        val mavenPath: String? by rootProject
        mavenPath?.let {
            maven {
                name = "mounted"
                url = uri(it)
            }
        }
    }
}

package figurafsb

import figurafsb.configurator.OptionsExt
import libs
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

the<OptionsExt>().then {
    java {
        withSourcesJar()

        toolchain {
            languageVersion = JavaLanguageVersion.of(it.javaToolchain.actual)
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release = it.javaVersion
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

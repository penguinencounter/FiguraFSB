package figurafsb

import figurafsb.configurator.OptionsExt

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
}

the<OptionsExt>().then {
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(it.javaToolchain.actual)
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release = it.javaVersion
    }
}

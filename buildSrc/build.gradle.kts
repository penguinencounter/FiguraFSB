import dev.panuszewski.gradle.pluginMarker

val javaVersion: String by project

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://maven.neoforged.net/releases") }
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://maven.minecraftforge.net/") }
    maven { url = uri("https://maven.architectury.dev/") }
}

dependencies {
    // plugins used in convention plugins
    implementation(pluginMarker(libs.plugins.architectury.plugin))
    implementation(pluginMarker(libs.plugins.architectury.loom))
    implementation(pluginMarker(libs.plugins.shadow))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}
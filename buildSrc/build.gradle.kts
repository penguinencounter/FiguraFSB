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
}

dependencies {
    // plugins used in convention plugins
//    implementation(pluginMarker(libs.plugins.shadow))
    implementation(pluginMarker(libs.plugins.neogradle.neoform))
    implementation(pluginMarker(libs.plugins.neogradle.userdev))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}
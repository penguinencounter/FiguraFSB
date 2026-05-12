import dev.panuszewski.gradle.pluginMarker

val javaVersion: String by project

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://maven.architectury.dev/") }
    maven { url = uri("https://maven.fabricmc.net/") }
    maven { url = uri("https://maven.minecraftforge.net/") }
    maven { url = uri("https://maven.neoforged.net/releases") }
    maven { url = uri("https://raw.githubusercontent.com/settingdust/maven/main/repository/") }
}

dependencies {
    // plugins used in convention plugins
    implementation(pluginMarker(libs.plugins.cloche))
//    implementation(pluginMarker(libs.plugins.shadow))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}
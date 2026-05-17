package figurafsb

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

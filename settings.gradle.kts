pluginManagement {
    repositories {
        // Repositories where you can get plugins
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.architectury.dev/") }
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.minecraftforge.net/") }
    }
}

rootProject.name = "FiguraFSB"

include(":library", ":minecraft")
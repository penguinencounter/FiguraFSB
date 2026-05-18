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

include(":fsb-api")
include(":minecraft:common:any")
include(":minecraft:fabric:any")

val versions = listOf(
    "1.16.5", "1.18.2", "1.19.2", "1.19.3", "1.19.4", "1.20.1",
    "1.20.2", "1.20.4", "1.20.6", "1.21.1", "1.21.3", "1.21.4"
)
val neoForgeAfter = versions.indexOf("1.20.2")

for ((i, version) in versions.withIndex()) {
    val hasNeo = i >= neoForgeAfter
    include(":minecraft:common:${version}")
    include(":minecraft:fabric:${version}")
//    include(":minecraft:forge:${version}")
//    if (hasNeo) include(":minecraft:neoforge:${version}")
}

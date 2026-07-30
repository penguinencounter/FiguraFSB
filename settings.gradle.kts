import java.util.*

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

/**
 * Load the build.properties file
 */
fun loadBuildProps(): Properties {
    val buildProps = Properties()
    val buildPropsFile = rootProject.projectDir.resolve("build.properties")
    if (!buildPropsFile.exists()) {
        logger.lifecycle("No build.properties file! Configuring all projects. (See README for details.)")
        return buildProps
    }
    buildPropsFile.reader().use(buildProps::load)

    return buildProps
}

val buildProps = loadBuildProps()

private enum class Platform {
    Fabric, Forge, NeoForge
}

private val platforms =
    buildProps["platforms"]?.toString()?.split(",")?.map(Platform::valueOf)?.toSet()
        ?: Platform.entries.toSet()

logger.lifecycle("Target platforms: $platforms")

rootProject.name = "FiguraFSB"

include(":fsb-api")
include(":minecraft:common:any")
include(":minecraft:common:modernish")
if (Platform.Fabric in platforms) include(":minecraft:fabric:any")
if (Platform.Forge in platforms) include(":minecraft:forge:any")
if (Platform.NeoForge in platforms) include(":minecraft:neoforge:any")

val allVersions = listOf(
    "1.16.5", "1.18.2", "1.19.2", "1.19.3", "1.19.4", "1.20.1",
    "1.20.2", "1.20.4", "1.20.6", "1.21.1", "1.21.3", "1.21.4"
)

val versions =
    buildProps["minecraft"]?.toString()?.split(",")
        ?.also {
            val missing = it.filter { v -> v !in allVersions }
            require(missing.isEmpty()) { "Invalid Minecraft version(s): $missing" }
        } ?: allVersions
logger.lifecycle("Target Minecraft versions: $versions")

val neoForgeAfter = allVersions.indexOf("1.20.2")
fun hasNeo(ver: String) = allVersions.indexOf(ver) >= neoForgeAfter

for (version in versions) {
    include(":minecraft:common:${version}")
    if (Platform.Fabric in platforms) include(":minecraft:fabric:${version}")
    if (Platform.Forge in platforms) include(":minecraft:forge:${version}")
    if (Platform.NeoForge in platforms && hasNeo(version)) include(":minecraft:neoforge:${version}")
}

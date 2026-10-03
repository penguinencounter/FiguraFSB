import figurafsb.getValue

plugins {
    idea
}


fun String.property() = providers.gradleProperty(this)

val modVersion: String by "modVersion".property()
project.version = modVersion
val rootGroup: String by "rootGroup".property()
project.group = rootGroup

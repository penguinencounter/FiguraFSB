/**
 * any fabric
 * --------------
 *
 * Only provides Fabric Loader on classpath.
 * Not a Minecraft configuration.
 */

package figurafsb.targets

import figurafsb.Names.Companion.names
import figurafsb.asNullable
import figurafsb.configurator.OptionsExt
import figurafsb.getValue
import figurafsb.versioning.fabricLoader
import figurafsb.yesno

plugins {
    id("figurafsb.standalone")
}

the<OptionsExt>().apply {
    java8()
}

dependencies {
    compileOnly("net.fabricmc:fabric-loader:$fabricLoader")
    compileOnly(project(":fsb-api"))
    compileOnly(project(":minecraft:common:any"))
}

fun String.property() = providers.gradleProperty(this)

names().run {
    project.version = version("fabric")

}
val artifactRoot: String by "artifactRoot".property()
val snapshot: String? by "snapshot".property().asNullable

val names = project.names()
project.version = names.version("fabric")
project.group = names.fabricGroup

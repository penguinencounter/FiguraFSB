/**
 * any fabric
 * --------------
 *
 * Only provides Fabric Loader on classpath.
 * Not a Minecraft configuration.
 */

package figurafsb.targets

import figurafsb.configurator.OptionsExt
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

val artifactRoot: String by project
val snapshot: String? by project
project.version = buildString {
    append(rootProject.version)
    append("+fabric")
    if (yesno(snapshot)) append("-SNAPSHOT")
}
project.group = rootProject.group

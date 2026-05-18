/**
 * any fabric
 * --------------
 *
 * Only provides Fabric Loader on classpath.
 * Not a Minecraft configuration.
 */

package figurafsb.targets

import figurafsb.configurator.FSBJavaToolchain
import figurafsb.configurator.OptionsExt
import figurafsb.versioning.fabricLoader

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

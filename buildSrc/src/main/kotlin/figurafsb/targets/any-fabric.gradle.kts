/**
 * any fabric
 * --------------
 *
 * Only provides Fabric Loader on classpath.
 * Not a Minecraft configuration.
 */

package figurafsb.targets

import figurafsb.versioning.FSBDeps

plugins {
    id("figurafsb.base")
}

dependencies {
    compileOnly(FSBDeps.fabricLoader())
}

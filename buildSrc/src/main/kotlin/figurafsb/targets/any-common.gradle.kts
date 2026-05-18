/**
 * any common
 * --------------
 *
 * nothing special, just Java.
 */

package figurafsb.targets

import figurafsb.configurator.OptionsExt


plugins {
    id("figurafsb.standalone")
}

the<OptionsExt>().apply {
    java8()
}

dependencies {
    compileOnly(project(":fsb-api"))
}

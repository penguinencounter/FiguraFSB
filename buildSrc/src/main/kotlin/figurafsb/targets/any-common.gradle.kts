/**
 * any common
 * --------------
 *
 * nothing special, just Java.
 */

package figurafsb.targets

import figurafsb.Names.Companion.names
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

val names = project.names()
project.version = names.version()
project.group = names.commonGroup

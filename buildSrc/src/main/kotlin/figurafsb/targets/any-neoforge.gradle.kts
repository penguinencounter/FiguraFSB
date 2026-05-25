package figurafsb.targets

import figurafsb.configurator.OptionsExt


plugins {
    id("figurafsb.standalone")
}

the<OptionsExt>().apply {
    java17()
}

dependencies {
    compileOnly(project(":fsb-api"))
    compileOnly(project(":minecraft:common:any"))
}

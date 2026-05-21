package figurafsb.targets

import figurafsb.configurator.OptionsExt


plugins {
    id("figurafsb.standalone")
}

the<OptionsExt>().apply {
    java8()
}
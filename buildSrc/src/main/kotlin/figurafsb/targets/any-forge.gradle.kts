package figurafsb.targets

import figurafsb.configurator.OptionsExt
import figurafsb.yesno


plugins {
    id("figurafsb.standalone")
}

the<OptionsExt>().apply {
    java8()
}

dependencies {
    compileOnly(project(":fsb-api"))
    compileOnly(project(":minecraft:common:any"))
}

val artifactRoot: String by project
val snapshot: String? by project
project.version = buildString {
    append(rootProject.version)
    append("+forge")
    if (yesno(snapshot)) append("-SNAPSHOT")
}
project.group = rootProject.group

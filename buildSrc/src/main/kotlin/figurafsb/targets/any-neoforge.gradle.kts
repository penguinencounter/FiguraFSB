package figurafsb.targets

import figurafsb.configurator.OptionsExt
import figurafsb.yesno


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

val artifactRoot: String by project
val snapshot: String? by project
project.version = buildString {
    append(rootProject.version)
    append("+neoforge")
    if (yesno(snapshot)) append("-SNAPSHOT")
}
project.group = rootProject.group

publishing {
    publications {
        register("maven", MavenPublication::class) {
            artifactId = "${artifactRoot}-neoforge-any"

            from(components["java"])
        }
    }
}
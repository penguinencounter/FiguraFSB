package figurafsb.targets

import figurafsb.asNullable
import figurafsb.configurator.OptionsExt
import figurafsb.getValue
import figurafsb.yesno
import org.gradle.kotlin.dsl.provideDelegate


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

fun String.property() = providers.gradleProperty(this)

val artifactRoot: String by "artifactRoot".property()
val snapshot: String? by "snapshot".property().asNullable

project.version = buildString {
    append(rootProject.version)
    append("+forge")
    if (yesno(snapshot)) append("-SNAPSHOT")
}
project.group = rootProject.group

import figurafsb.asNullable
import figurafsb.getValue
import figurafsb.yesno

plugins {
    id("figurafsb.targets.any-common")
}

dependencies {
//    compileOnly(libs.gson)
}

fsbOptions.done()

fun String.property() = providers.gradleProperty(this)

val artifactRoot: String by "artifactRoot".property()
val snapshot: String? by "snapshot".property().asNullable

project.version = buildString {
    append(rootProject.version)
    if (yesno(snapshot)) append("-SNAPSHOT")
}
project.group = rootProject.group

publishing {
    publications {
        register("maven", MavenPublication::class) {
            artifactId = "${artifactRoot}-server-api"

            from(components["java"])
        }
    }
}

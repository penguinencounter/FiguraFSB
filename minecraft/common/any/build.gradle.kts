import figurafsb.yesno

plugins {
    id("figurafsb.targets.any-common")
}

dependencies {
//    compileOnly(libs.gson)
}

fsbOptions.done()

val artifactRoot: String by project
val snapshot: String? by project
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

plugins {
    id("figurafsb.standalone")
}

dependencies {
    compileOnlyApi(libs.brigadier)
    compileOnlyApi(libs.gson)
    compileOnlyApi(libs.guava)
}

fsbOptions.configure {
    java8()
}

val artifactRoot: String by project
project.version = rootProject.version
project.group = rootProject.group

publishing {
    publications {
        register("maven", MavenPublication::class) {
            artifactId = "${artifactRoot}-api"

            from(components["java"])
        }
    }
}

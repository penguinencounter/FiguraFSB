plugins {
    id("figurafsb.standalone")
}

dependencies {
    compileOnlyApi(libs.brigadier)
    compileOnlyApi(libs.gson)
}

fsbOptions.configure {
    java8()
}

val artifactRoot: String by project
project.version = rootProject.version

publishing {
    publications {
        register("maven", MavenPublication::class) {
            artifactId = "${artifactRoot}-api"

            from(components["java"])
        }
    }
}

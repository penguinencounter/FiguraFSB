import figurafsb.yesno

plugins {
    id("figurafsb.standalone")
}

dependencies {
    compileOnlyApi(libs.brigadier)
    compileOnlyApi(libs.gson)
    compileOnlyApi(libs.guava)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
    testImplementation(libs.classgraph)
}

fsbOptions.configure {
    java8()
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    maxHeapSize = "1G"

    testLogging {
        events("passed")
    }
}

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
            artifactId = "${artifactRoot}-api"

            from(components["java"])
        }
    }
}

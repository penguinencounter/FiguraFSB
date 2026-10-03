import figurafsb.asNullable
import figurafsb.getValue
import figurafsb.yesno
import org.gradle.kotlin.dsl.provideDelegate

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
            artifactId = "${artifactRoot}-api"

            from(components["java"])
        }
    }
}

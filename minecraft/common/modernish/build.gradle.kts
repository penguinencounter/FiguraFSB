// "modernish": not 1.16.5

plugins {
    id("figurafsb.targets.any-common")
}

dependencies {
    // only available after 1.16.5; 1.16.5 used direct Log4J instead
    compileOnly(libs.slf4j)
    implementation(project(":minecraft:common:any"))
    implementation(project(":fsb-api"))
}

fsbOptions.configure {
    java17()
}

package figurafsb

import libs

plugins {
    id("figurafsb.base")
}

dependencies {
    compileOnly(libs.slf4j)
}

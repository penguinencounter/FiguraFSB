package figurafsb

import libs

plugins {
    id("figurafsb.base")
}

dependencies {
    // TODO FIXME: this is NOT ok on 1.16.5.
    compileOnly(libs.slf4j)
}

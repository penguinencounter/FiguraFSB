plugins {
    id("figurafsb.targets.forge-version")
}

fsbOptions.configure {
    java8()

    minecraft {
        version = "1.16.5"
        upstream(":common:1.16.5")
    }
}
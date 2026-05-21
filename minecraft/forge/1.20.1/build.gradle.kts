plugins {
    id("figurafsb.targets.forge-version")
}

fsbOptions.configure {
    java17()

    minecraft {
        version = "1.20.1"
        upstream(":common:1.20.1")
    }
}
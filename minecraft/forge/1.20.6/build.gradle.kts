plugins {
    id("figurafsb.targets.forge-version")
}

fsbOptions.configure {
    java21()

    minecraft {
        version = "1.20.6"
        upstream(":common:1.20.6")
    }
}
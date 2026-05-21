plugins {
    id("figurafsb.targets.forge-version")
}

fsbOptions.configure {
    java21()

    minecraft {
        version = "1.21.1"
        upstream(":common:1.21.1")
    }
}
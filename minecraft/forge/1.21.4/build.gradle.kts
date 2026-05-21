plugins {
    id("figurafsb.targets.forge-version")
}

fsbOptions.configure {
    java21()

    minecraft {
        version = "1.21.4"
        upstream(":common:1.21.4")
    }
}
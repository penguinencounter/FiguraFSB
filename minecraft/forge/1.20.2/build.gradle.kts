plugins {
    id("figurafsb.targets.forge-version")
}

fsbOptions.configure {
    java17()

    minecraft {
        version = "1.20.2"
        upstream(":common:1.20.2")
    }
}
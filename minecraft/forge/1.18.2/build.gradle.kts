plugins {
    id("figurafsb.targets.forge-version")
}

fsbOptions.configure {
    java17()

    minecraft {
        version = "1.18.2"
        upstream(":common:1.18.2")
    }
}
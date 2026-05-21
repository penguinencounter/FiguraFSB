plugins {
    id("figurafsb.targets.forge-version")
}

fsbOptions.configure {
    java17()

    minecraft {
        version = "1.19.2"
        upstream(":common:1.19.2")
    }
}
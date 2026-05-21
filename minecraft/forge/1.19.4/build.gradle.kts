plugins {
    id("figurafsb.targets.forge-version")
}

fsbOptions.configure {
    java17()

    minecraft {
        version = "1.19.4"
        upstream(":common:1.19.4")
    }
}
plugins {
    id("figurafsb.targets.fabric-version")
}

fsbOptions.configure {
    java17()

    minecraft {
        version = "1.20.1"
    }
}

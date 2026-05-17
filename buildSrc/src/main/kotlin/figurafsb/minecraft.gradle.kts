package figurafsb

plugins {
    id("figurafsb.consumable")
    id("earth.terrarium.cloche")
}

repositories {
    cloche.librariesMinecraft()
    mavenCentral()

    cloche {
        main()
        mavenNeoforgedMeta()
        mavenNeoforged()
        mavenFabric()
        mavenForge()
        mavenParchment()
    }
}
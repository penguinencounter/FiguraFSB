package figurafsb.versioning

const val fabricLoader = "0.16.10"

val VERSIONS = versions {
    /**
     * parchment: https://parchmentmc.org/docs/getting-started
     * fabric api: https://fabricmc.net/develop/
     * forge: https://github.com/FiguraMC/Figura/blob/1.20/gradle.properties
     */
    "1.16.5" {
        parchment = "2022.03.06"
        fabric(fabricLoader) {
            api = "0.42.0+1.16"
        }
        forge("36.2.41", 36) {}
    }
    "1.18.2" {
        parchment = "2022.11.06"
        fabric(fabricLoader) {
            api = "0.77.0".versioned
        }
        forge("40.2.10", 40) {}
    }
    "1.19.2" {
        parchment = "2022.11.27"
        fabric(fabricLoader) {
            api = "0.77.0".versioned
        }
        forge("43.2.21", 43) {}
    }
    "1.19.3" {
        parchment = "2023.06.25"
        fabric(fabricLoader) {
            api = "0.76.1".versioned
        }
        forge("44.1.23", 44) {}
    }
    "1.19.4" {
        parchment = "2023.06.26"
        fabric(fabricLoader) {
            api = "0.87.2".versioned
        }
        forge("45.1.16", 45) {}
    }
    "1.20.1" {
        parchment = "2023.09.03"
        fabric(fabricLoader) {
            api = "0.92.9".versioned
        }
        forge("47.1.43", 47) {}
    }
    "1.20.2" {
        parchment = "2023.12.10"
        fabric(fabricLoader) {
            api = "0.91.6".versioned
        }
        forge("48.0.13", 48) {}
        neoforge("20.2.93")
    }
    "1.20.4" {
        parchment = "2024.04.14"
        fabric(fabricLoader) {
            api = "0.97.3".versioned
        }
        forge("49.0.8", 49) {}
        neoforge("20.4.238")
    }
    "1.20.6" {
        parchment = "2024.06.16"
        fabric(fabricLoader) {
            api = "0.100.8".versioned
        }
        forge("50.0.8", 50) {}
        neoforge("20.6.119")
    }
    "1.21.1" {
        parchment = "2024.11.17"
        fabric(fabricLoader) {
            api = "0.116.12".versioned
        }
        forge("52.0.28", 52) {}
        neoforge("21.1.80")
    }
    "1.21.3" {
        parchment = "2024.12.07"
        fabric(fabricLoader) {
            api = "0.114.1".versioned
        }
        forge("53.0.14", 53)
    }
    "1.21.4" {
        parchment = "2025.03.23"
        fabric(fabricLoader) {
            api = "0.119.4".versioned
        }
        forge("54.0.6", 54)
    }
}

fun versionFor(minecraft: String) =
    VERSIONS[minecraft] ?: throw IllegalArgumentException("Unknown Minecraft version $minecraft")
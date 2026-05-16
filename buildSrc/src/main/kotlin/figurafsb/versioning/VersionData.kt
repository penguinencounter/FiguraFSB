package figurafsb.versioning

const val fabricLoader = "0.15.10"

val VERSIONS = versions {
    "1.16.5" {
        fabric(fabricLoader) {
            api = "0.42.0+1.16"
        }
        forge("36.2.41", 36)
    }
    "1.18.2" {
        fabric(fabricLoader) {
            api = "0.77.0".versioned
        }
        forge("40.2.10", 40)
    }
    "1.19.2" {
        fabric(fabricLoader) {
            api = "0.77.0".versioned
        }
        forge("43.2.21", 43)
    }
    "1.19.3" {
        fabric(fabricLoader) {
            api = "0.76.1".versioned
        }
        forge("44.1.23", 44)
    }
    "1.19.4" {
        fabric(fabricLoader) {
            api = "0.87.2".versioned
        }
        forge("45.1.16", 45)
    }
    "1.20.1" {
        fabric(fabricLoader) {
            api = "0.92.9".versioned
        }
        forge("47.1.43", 47)
    }
    "1.20.2" {
        fabric(fabricLoader) {
            api = "0.91.6".versioned
        }
        forge("48.0.13", 48)
//        neoforge("20.2.93")
    }
    "1.20.4" {
        fabric(fabricLoader) {
            api = "0.97.3".versioned
        }
        forge("49.0.8", 49)
//        neoforge("20.4.238") // NO
        neoforge("20.4.239") // YES
//        neoforge("20.4.241") // YES
//        neoforge("20.4.244") // YES
//        neoforge("20.4.251") // YES
    }
    "1.20.6" {
        fabric(fabricLoader) {
            api = "0.100.8".versioned
        }
        forge("50.0.8", 50)
        neoforge("20.6.122")
    }
    "1.21.1" {
        fabric(fabricLoader) {
            api = "0.116.12".versioned
        }
        forge("52.1.0", 52)
        neoforge("21.1.80")
    }
    "1.21.3" {
        fabric(fabricLoader) {
            api = "0.114.1".versioned
        }
        forge("53.0.14", 53)
        neoforge("21.3.28-beta")
    }
    "1.21.4" {
        fabric(fabricLoader) {
            api = "0.119.4".versioned
        }
        forge("54.0.6", 54)
        neoforge("21.4.92-beta")
    }
}

fun versionFor(minecraft: String) =
    VERSIONS[minecraft] ?: throw IllegalArgumentException("Unknown Minecraft version $minecraft")
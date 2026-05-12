package figurafsb.versioning

const val fabricLoader = "0.15.10"

val VERSIONS = versions {
//    "1.16.5" {
//        fabric(fabricLoader) {
//            api = "0.42.0+1.16"
//        }
//        forge("36.2.41", 36) {}
//    }
    "1.18.2" {
        fabric(fabricLoader) {
            api = "0.77.0".versioned
        }
        forge("40.2.10", 40) {}
    }
    "1.19.2" {
        fabric(fabricLoader) {
            api = "0.77.0".versioned
        }
        forge("43.2.21", 43) {}
    }
    "1.19.3" {
        fabric(fabricLoader) {
            api = "0.76.1".versioned
        }
        forge("44.1.23", 44) {}
    }
    "1.19.4" {
        fabric(fabricLoader) {
            api = "0.87.2".versioned
        }
        forge("45.1.16", 45) {}
    }
    "1.20.1" {
        fabric(fabricLoader) {
            api = "0.92.8".versioned
        }
        forge("47.1.43", 47) {}
    }
}

fun versionFor(minecraft: String) =
    VERSIONS[minecraft] ?: throw IllegalArgumentException("Unknown Minecraft version $minecraft")
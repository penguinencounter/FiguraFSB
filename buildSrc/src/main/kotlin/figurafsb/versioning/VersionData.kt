package figurafsb.versioning

const val fabricLoader = "0.15.10"

val VERSIONS = versions {
    "1.16.5" {
        fabric(fabricLoader) {
            api = "0.42.0+1.16"
        }
    }
    "1.18.2" {
        fabric(fabricLoader) {
            api = "0.77.0".versioned
        }
    }
    "1.19.2" {
        fabric(fabricLoader) {
            api = "0.77.0".versioned
        }
    }
    "1.19.3" {
        fabric(fabricLoader) {
            api = "0.76.1".versioned
        }
    }
    "1.19.4" {
        fabric(fabricLoader) {
            api = "0.87.2".versioned
        }
    }
    "1.20.1" {
        fabric(fabricLoader) {
            api = "0.92.8".versioned
        }
//        forge("47.1.43", 47) {
//
//        }
    }
}

fun versionFor(minecraft: String) = VERSIONS[minecraft] ?: throw IllegalArgumentException("Unknown Minecraft version $minecraft")
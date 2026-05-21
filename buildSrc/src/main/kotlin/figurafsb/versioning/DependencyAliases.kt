package figurafsb.versioning

class FSBDependencyContext(private val version: Version) {
    private val fabric by lazy {
        checkNotNull(version.fabric) {
            "using a fabric dependency in this build, but fabric is not configured for this version (${version.minecraft})"
        }
    }
    private val forge by lazy {
        checkNotNull(version.forge) {
            "using a forge dependency in this build, but forge is not configured for this version (${version.minecraft})"
        }
    }
    private val neoforge by lazy {
        checkNotNull(version.neoforge) {
            "using a neoforge dependency in this build, but neoforge is not configured for this version (${version.minecraft})"
        }
    }

    fun fabricForced() = fabric.forced

    fun minecraft() = "com.mojang:minecraft:${version.minecraft}"

    fun parchment() = "org.parchmentmc.data:parchment-${version.minecraft}:${version.parchment}@zip"

    fun fabricApi() =
        "net.fabricmc.fabric-api:fabric-api:${fabric.api}"

    fun fabricLoader() = "net.fabricmc:fabric-loader:${fabric.loader}"

    fun forgeLoader() = "net.minecraftforge:forge:${version.minecraft}-${forge.loader}"
}

inline fun Version.dependencyContext(c: (d: FSBDependencyContext) -> Unit) = c(FSBDependencyContext(this))
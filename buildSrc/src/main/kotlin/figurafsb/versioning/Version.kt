package figurafsb.versioning

@DslMarker
private annotation class VersionDsl
private typealias recv<T> = T.() -> Unit

class Version(
    val minecraft: String,
    val parchment: String,
    val mixin: String,
    val fabric: FabricDependencies?,
    val forge: ForgeDependencies?,
    val neoforge: NeoForgeDependencies?,
)

class FabricDependencies(
    val loader: String,
    val api: String,
    val forced: Boolean
)
class ForgeDependencies(
    val loader: String,
    val fml: Int,
)
class NeoForgeDependencies(
    val loader: String,
)

@VersionDsl
class FabricDependenciesBuilder(private val mc: String, private val loader: String) {
    var api: String? = null
    var forced: Boolean = false
    val String.versioned: String get() = "$this+$mc"

    fun build() = FabricDependencies(
        loader = loader,
        api = checkNotNull(api),
        forced = forced,
    )
}

@VersionDsl
class ForgeDependenciesBuilder(private val loader: String, private val fml: Int) {
    fun build() = ForgeDependencies(
        loader = loader,
        fml = fml
    )
}

@VersionDsl
class NeoForgeDependenciesBuilder(private val loader: String) {
    fun build() = NeoForgeDependencies(
        loader = loader
    )
}

@VersionDsl
class VersionBuilder(private val minecraft: String) {
    var parchment: String? = null
    var mixin: String? = null

    var fabric: FabricDependencies? = null
    var forge: ForgeDependencies? = null
    var neoforge: NeoForgeDependencies? = null

    fun build() = Version(
        minecraft = minecraft,
        parchment = checkNotNull(parchment),
        mixin = checkNotNull(mixin),
        fabric = fabric,
        forge = forge,
        neoforge = neoforge,
    )

    fun fabric(loader: String, c: recv<FabricDependenciesBuilder> = {}) {
        fabric = FabricDependenciesBuilder(minecraft, loader).also(c).build()
    }

    fun forge(loader: String, fml: Int, c: recv<ForgeDependenciesBuilder> = {}) {
        forge = ForgeDependenciesBuilder(loader, fml).also(c).build()
    }

    fun neoforge(loader: String, c: recv<NeoForgeDependenciesBuilder> = {}) {
        neoforge = NeoForgeDependenciesBuilder(loader).also(c).build()
    }
}

inline fun version(minecraft: String, c: recv<VersionBuilder>) = VersionBuilder(minecraft).also(c).build()

@VersionDsl
class MultiVersionBuilder {
    val versions: MutableMap<String, Version> = mutableMapOf()

    operator fun String.invoke(c: recv<VersionBuilder>) {
        if (this.trim() != this) throw IllegalArgumentException("Minecraft version should not have leading or trailing spaces")
        if (this in versions) throw IllegalArgumentException("Minecraft versions already registered")
        versions[this] = version(this, c)
    }
}

inline fun versions(c: recv<MultiVersionBuilder>) = run {
    val result = MultiVersionBuilder().also(c).versions.toMap()
    result
}
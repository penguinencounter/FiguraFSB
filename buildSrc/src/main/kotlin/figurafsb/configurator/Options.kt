package figurafsb.configurator

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

typealias OptionsHandler = (options: ReifiedOptions) -> Unit

private interface CanReify<to> {
    fun reify(): to
}

enum class FSBJavaToolchain(val actual: Int) {
    JDK17(17),
    JDK21(21)
}

enum class FSBPlatform(val lowercase: String, val capitalized: String) {
    FABRIC("fabric", "Fabric"),
    FORGE("forge", "Forge"),
    NEOFORGE("neoforge", "NeoForge")
}

class ReifiedOptions(
    val javaVersion: Int,
    val javaToolchain: FSBJavaToolchain,
    val minecraft: ReifiedMinecraftOptions?
)

class ReifiedMinecraftOptions(
    val minecraftVersion: String,
    val platform: FSBPlatform,
    val upstreams: List<String>,
    val plainUpstreams: List<String>
)

open class MinecraftOptions @Inject constructor(private val objects: ObjectFactory): CanReify<ReifiedMinecraftOptions> {
    val version: Property<String> = objects.property()
    val platform: Property<FSBPlatform> = objects.property()
    val upstreams: ListProperty<String> = objects.listProperty()
    val plainUpstreams: ListProperty<String> = objects.listProperty()

    /**
     * add an upstream. don't include the `:minecraft` prefix on it.
     */
    fun upstream(project: String) = upstreams.add(":minecraft:$project")

    /**
     * add an upstream that isn't managed by loom. don't include the `:minecraft` prefix.
     */
    fun plain(project: String) = plainUpstreams.add(":minecraft:$project")

    override fun reify() = ReifiedMinecraftOptions(
        minecraftVersion = version.get(),
        platform = platform.get(),
        upstreams = upstreams.get(),
        plainUpstreams = plainUpstreams.get(),
    )
}

open class OptionsExt @Inject constructor(private val objects: ObjectFactory) : CanReify<ReifiedOptions> {
    private val onCompleteHandlers = mutableListOf<OptionsHandler>()
    private var committed: ReifiedOptions? = null

    // Options
    val javaVersion: Property<Int> = objects.property()
    val javaToolchain: Property<FSBJavaToolchain> = objects.property()
    val minecraft: Property<MinecraftOptions> = objects.property<MinecraftOptions>()

    fun java8() {
        javaVersion.set(8)
        javaToolchain.set(FSBJavaToolchain.JDK17)
    }

    fun java17() {
        javaVersion.set(17)
        javaToolchain.set(FSBJavaToolchain.JDK17)
    }

    fun java21() {
        javaVersion.set(21)
        javaToolchain.set(FSBJavaToolchain.JDK21)
    }

    fun minecraft(c: MinecraftOptions.() -> Unit) {
        if (!minecraft.isPresent) minecraft.set(objects.newInstance<MinecraftOptions>())
        minecraft.get().apply(c)
    }

    // api
    override fun reify() = ReifiedOptions(
        javaVersion = javaVersion.get(),
        javaToolchain = javaToolchain.get(),
        minecraft = minecraft.orNull?.reify()
    )

    fun then(act: OptionsHandler) {
        if (committed != null) return act(committed!!)
        onCompleteHandlers += act
    }

    fun done() {
        if (committed != null) throw IllegalStateException("i've already been configured and committed! you're too late.")
        val result = reify()
        committed = result
        onCompleteHandlers.forEach { it(result) }
        onCompleteHandlers.clear()
    }

    fun configure(c: OptionsExt.() -> Unit) {
        this.c()
        done()
    }

    fun postConfigure() {
        if (committed == null && !onCompleteHandlers.isEmpty())
            throw IllegalStateException("${onCompleteHandlers.size} waiting for 'fsbOptions' to be configured, but that didn't happen in time. add a fsbOptions.configure {} block?")
    }
}
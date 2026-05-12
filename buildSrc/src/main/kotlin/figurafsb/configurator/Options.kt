package figurafsb.configurator

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

typealias OptionsHandler = (options: ReifiedOptions) -> Unit

private interface CanReify<to> {
    fun reify(): to
}

enum class FSBJavaToolchain(val actual: Int) {
    JDK17(17),
    JDK21(21)
}

class ReifiedOptions(
    val javaVersion: Int,
    val javaToolchain: FSBJavaToolchain,
)

sealed class ReifiedMinecraftOptions(
    val minecraftVersion: String,
)


abstract class OptionsExt @Inject constructor(objects: ObjectFactory) : CanReify<ReifiedOptions> {
    private val onCompleteHandlers = mutableListOf<OptionsHandler>()
    private var committed: ReifiedOptions? = null

    // Options
    abstract var javaVersion: Property<Int>
    abstract var javaToolchain: Property<FSBJavaToolchain>

    // api
    override fun reify() = ReifiedOptions(
        javaVersion = javaVersion.get(),
        javaToolchain = javaToolchain.get(),
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

    fun postConfigure() {
        if (committed == null && !onCompleteHandlers.isEmpty())
            throw IllegalStateException("${onCompleteHandlers.size} waiting for 'fsbOptions' to be configured, but that didn't happen in time")
    }
}
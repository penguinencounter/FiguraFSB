package figurafsb

import org.gradle.api.Project
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.provideDelegate
import java.util.*

class Names private constructor(private val providers: ProviderFactory, private val rootVersion: String) {
    companion object {
        private val map = Collections.synchronizedMap(WeakHashMap<Project, Names>())

        fun get(proj: Project): Names = map.computeIfAbsent(proj) {
            Names(proj.providers, proj.rootProject.version.toString())
        }

        fun Project.names() = get(this)
        fun Project.withNames(conf: Names.() -> Unit) = get(this).run(conf)
    }

    fun String.property() = providers.gradleProperty(this)

    val artifactRoot: String by "artifactRoot".property()
    val snapshot: String? by "snapshot".property().asNullable

    val rootGroup: String by "rootGroup".property()
    val commonGroup: String by lazy {
        buildString {
            append(rootGroup)
            append(".common")
        }
    }
    val fabricGroup: String by lazy {
        buildString {
            append(rootGroup)
            append(".fabric")
        }
    }
    val forgeGroup: String by lazy {
        buildString {
            append(rootGroup)
            append(".forge")
        }
    }
    val neoforgeGroup: String by lazy {
        buildString {
            append(rootGroup)
            append(".neoforge")
        }
    }

    fun version(ext: String? = null) = buildString {
        append(rootVersion)
        if (ext != null) append("+${ext}")
        if (yesno(snapshot)) append("-SNAPSHOT")
    }
}
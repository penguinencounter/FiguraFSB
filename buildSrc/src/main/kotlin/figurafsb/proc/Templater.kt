package figurafsb.proc

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.logging.Logging
import java.io.Serializable
import java.util.Hashtable

data class Templater(val replacements: Map<String, String?>) : Serializable {
    companion object {
        val PATTERN = Regex("\\$\\{([a-zA-Z0-9_.]+?)}")
        private val log = Logging.getLogger(Templater::class.java);

        @Suppress("unused")
        private const val serialVersionUID: Long = 0x4514_5B9D_ED82_6BA4L
    }

    fun process(source: String, notetaker: (String) -> Unit) = source.replace(PATTERN) {
        val inner = it.groups[1]?.value
        val result = inner?.let(replacements::get)
        if (result != null)
            notetaker(" ~~ {$inner}=$result")
        else
            notetaker(" ~~ {$inner} unavailable")
        result ?: it.value
    }

    /**
     * get a [ReplaceTokens] options map for Gradle corresponding to this Templater
     */
    fun toReplaceTokensOptions() = arrayOf(
        "tokens" to Hashtable(replacements.filter { (_, v) -> v != null }),
        "beginToken" to $$"${",
        "endToken" to "}"
    )
}
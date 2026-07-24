package figurafsb.proc

import org.gradle.api.logging.Logging
import java.io.Serializable

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
}
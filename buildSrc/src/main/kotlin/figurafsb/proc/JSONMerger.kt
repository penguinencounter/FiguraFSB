package figurafsb.proc

import com.github.jengelman.gradle.plugins.shadow.relocation.relocatePath
import com.github.jengelman.gradle.plugins.shadow.transformers.CacheableTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.PatternFilterableResourceTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import kotlinx.serialization.json.*
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream
import org.gradle.api.file.FileTreeElement
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.util.PatternSet

@CacheableTransformer
class JSONMerger
@JvmOverloads constructor(
    patternSet: PatternSet =
        PatternSet().include(JSON_ANY),
    @Input val templater: Templater,
) : PatternFilterableResourceTransformer(patternSet) {

    companion object {
        const val JSON_ANY = "**/*.json"
        const val KEY = "__resource_priority"

        internal val log = Logging.getLogger(JSONMerger::class.java)
    }

    @get:Internal
    internal val entries: MutableMap<String, Entry> = mutableMapOf()

    internal class Entry(val priority: Double, val content: String)

    override fun canTransformResource(element: FileTreeElement) = element.file.extension == "json"

    private fun walkObjects(el: JsonElement) = object : Iterator<JsonObject> {
        private val queue = mutableListOf(el)
        private var nextCache: JsonObject? = innerNext()

        private fun descend(e: JsonElement) = when (e) {
            is JsonObject -> queue.addAll(e.values)
            is JsonArray -> queue.addAll(e)
            else -> {}
        }

        private fun innerNext(): JsonObject? {
            while (true) {
                val el = queue.removeFirstOrNull() ?: return null
                descend(el)
                if (el is JsonObject) return el
            }
        }

        override fun next(): JsonObject {
            val result = nextCache
            nextCache = innerNext()
            return result ?: throw IllegalStateException("out of items")
        }

        override fun hasNext() = nextCache != null

    }

    // "intake" function
    override fun transform(context: TransformerContext) {
        val k = context.relocators.relocatePath(context.path)
        entries.compute(k) { _, prev ->
            val content = context.inputStream.reader(Charsets.UTF_8).readText()
            val priorities = walkObjects(Json.parseToJsonElement(content))
                .asSequence()
                .filter { it.containsKey(KEY) }
                .map {
                    (it[KEY] as? JsonPrimitive)
                        ?.double
                        ?.let { v -> if (v.isFinite()) v else null }
                        ?: throw IllegalStateException("$KEY with non-numeric value in ${context.path}")
                }
                .toList()

            val priority = when (priorities.size) {
                0 -> 0.0
                1 -> priorities.first()
                else -> throw IllegalStateException("more than one $KEY in ${context.path}")
            }

            val notes = mutableListOf<String>()

            lateinit var result: Entry
            if (prev == null || priority >= prev.priority) {
                if (prev != null && priority > prev.priority)
                    notes.add(" ✓  %50s (%s), previous best: %s".format(context.path, priority, prev.priority))
                result = Entry(priority, templater.process(content, notes::add))
            } else {
                notes.add("  ✗ %50s (%s),  current best: %s".format(context.path, priority, prev.priority))
                result = prev
            }
            if (notes.isNotEmpty()) log.lifecycle(buildString {
                appendLine("$k:")
                notes.forEach(::appendLine)
            })

            result
        }
    }

    override fun hasTransformedResource() = entries.isNotEmpty()

    override fun modifyOutputStream(os: ZipOutputStream, preserveFileTimestamps: Boolean) {
        for ((path, data) in entries) {
            os.putNextEntry(ZipEntry(path))
            os.write(data.content.toByteArray(Charsets.UTF_8))
            os.closeEntry()
        }
    }
}
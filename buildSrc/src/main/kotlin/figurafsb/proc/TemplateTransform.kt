package figurafsb.proc

import com.github.jengelman.gradle.plugins.shadow.relocation.relocatePath
import com.github.jengelman.gradle.plugins.shadow.transformers.CacheableTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.PatternFilterableResourceTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipOutputStream
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.util.PatternSet

@CacheableTransformer
class TemplateTransform(
    patternSet: PatternSet,
    @get:Input val templater: Templater,
) : PatternFilterableResourceTransformer(patternSet) {

    companion object {
        internal val log = Logging.getLogger(TemplateTransform::class.java)
    }

    @get:Internal
    internal val entries: MutableMap<String, String> = mutableMapOf()

    override fun transform(context: TransformerContext) {
        val k = context.relocators.relocatePath(context.path)
        entries.compute(k) {_, old ->
            old?.let {
                log.warn("Overwriting duplicate file at $k")
            }

            val notes = mutableListOf<String>()
            val result = templater.process(context.inputStream.reader(Charsets.UTF_8).readText(), notes::add)
            if (notes.isNotEmpty()) log.lifecycle(buildString {
                appendLine("$k:")
                notes.forEach(::appendLine)
            })

            result
        }
    }

    override fun modifyOutputStream(os: ZipOutputStream, preserveFileTimestamps: Boolean) {
        for ((path, data) in entries) {
            os.putNextEntry(ZipEntry(path))
            os.write(data.toByteArray(Charsets.UTF_8))
            os.closeEntry()
        }
    }
}
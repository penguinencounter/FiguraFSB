package figurafsb.proc

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.util.Constants
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.util.PatternSet
import org.gradle.jvm.tasks.Jar

fun ShadowJar.shadowDefaults(templater: Templater) {
    duplicatesStrategy = DuplicatesStrategy.WARN

    mergeServiceFiles()
    filesMatching("META-INF/services/**") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    transform(JSONMerger(templater = templater))
    filesMatching("**/*.json") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    transform(TemplateTransform(templater = templater, patternSet = PatternSet().include("**/*.toml")))
    filesMatching("**/*.toml") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

fun Jar.enableRemap(namespace: String = "intermediary", remapType: String = "STATIC") {
    manifest {
        attributes(mapOf(
            Constants.Manifest.REMAP_KEY to "true",
            Constants.Manifest.MAPPING_NAMESPACE to namespace,
            Constants.Manifest.MIXIN_REMAP_TYPE to remapType,
        ))
    }
}
package figurafsb.proc

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.util.PatternSet

fun ShadowJar.shadowDefaults(templater: Templater) {
    duplicatesStrategy = DuplicatesStrategy.WARN
    exclude("architectury.common.json")

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
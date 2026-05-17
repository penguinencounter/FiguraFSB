package figurafsb

fun projToConfName(name: String, prefixes: List<String> = emptyList()) = name.split(":", "-").let {
    val combined = prefixes + it
    combined.first() + combined.asSequence().drop(1).map { it.replaceFirstChar(Char::uppercase) }.joinToString("")
}

private val theMinecraftPrefix = Regex("^:?minecraft:")
fun String.removeMinecraftPrefix() = this.replace(theMinecraftPrefix, "")

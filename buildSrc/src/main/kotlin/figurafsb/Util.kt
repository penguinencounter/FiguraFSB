package figurafsb

fun projToConfName(name: String, prefixes: List<String> = emptyList()) = name.split(":", "-").let {
    val combined = prefixes + it
    combined.first() + combined.asSequence().drop(1).joinToString("") { it.replaceFirstChar(Char::uppercase) }
}

private val theMinecraftPrefix = Regex("^:?minecraft:")
fun String.removeMinecraftPrefix() = this.replace(theMinecraftPrefix, "")

private val yes = listOf("yes", "true", "y", "1")
private val no = listOf("no", "false", "n", "0")

/**
 * Convert a human-input boolean string into an actual boolean. Ideally use `true` or `false`,
 * but if you're feeling fun, `yes` and `no` also work
 */
fun yesno(s: String?, default: Boolean = false) = when (s?.lowercase()) {
    null -> default
    in yes -> true
    in no -> false
    else -> throw IllegalArgumentException("couldn't parse ${s.lowercase()} as a boolean value (try 'yes', 'true', 'no', 'false')")
}

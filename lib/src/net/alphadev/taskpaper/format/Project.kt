package net.alphadev.taskpaper.format

@ConsistentCopyVisibility
public data class Project internal constructor(
    override val text: String,
    override val indentLevel: Int,
    override val tags: Tags = emptyMap()
) : Item() {
    internal constructor(
        text: String,
        indentLevel: Int,
        vararg tags: Pair<String, List<String>>
    ) : this(text, indentLevel, mapOf(*tags))
}

package net.alphadev.taskpaper.format

data class Note(
    override val text: String,
    override val indentLevel: Int,
    override val tags: Tags = emptyMap()
) : Item() {
    constructor(
        text: String,
        indentLevel: Int,
        vararg tags: Pair<String, List<String>>
    ) : this(text, indentLevel, mapOf(*tags))
}

package net.alphadev.taskpaper.format

data class Task(
    override val text: String,
    override val indentLevel: Int,
    override val tags: Map<String, String?> = emptyMap()
) : Item()

package net.alphadev.taskpaper.format

data class Project(
    override val text: String,
    override val indentLevel: Int,
    override val tags: Tags = emptyMap()
) : Item()

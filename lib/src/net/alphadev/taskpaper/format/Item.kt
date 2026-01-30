package net.alphadev.taskpaper.format

sealed class Item {
    abstract val text: String
    abstract val indentLevel: Int
    abstract val tags: Tags
}

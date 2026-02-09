package net.alphadev.taskpaper.format

public sealed class Item {
    public abstract val text: String
    internal abstract val indentLevel: Int
    public abstract val tags: Tags
}

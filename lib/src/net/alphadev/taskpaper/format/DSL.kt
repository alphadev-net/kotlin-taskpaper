package net.alphadev.taskpaper.format

@DslMarker
annotation class TaskPaperDsl

@TaskPaperDsl
class TaskPaperBuilder {
    private val items = mutableListOf<Item>()
    private var currentIndent = 0

    fun project(text: String, block: ItemBuilder.() -> Unit = {}) {
        val builder = ItemBuilder(currentIndent)
        builder.block()
        items.add(Project(text, currentIndent, builder.tags))
        items.addAll(builder.children)
    }

    fun task(text: String, block: ItemBuilder.() -> Unit = {}) {
        val builder = ItemBuilder(currentIndent)
        builder.block()
        items.add(Task(text, currentIndent, builder.tags))
        items.addAll(builder.children)
    }

    fun note(text: String, block: ItemBuilder.() -> Unit = {}) {
        val builder = ItemBuilder(currentIndent)
        builder.block()
        items.add(Note(text, currentIndent, builder.tags))
        items.addAll(builder.children)
    }

    fun build() = TaskPaper(items)
}

@TaskPaperDsl
class ItemBuilder(private val indent: Int) {
    internal val tags = mutableMapOf<String, List<String>>()
    internal val children = mutableListOf<Item>()

    fun tag(name: String, block: TagBuilder.() -> Unit = {}) {
        val builder = TagBuilder()
        builder.block()
        tags[name] = builder.values
    }

    fun project(text: String, block: ItemBuilder.() -> Unit = {}) {
        val builder = ItemBuilder(indent + 1)
        builder.block()
        children.add(Project(text, indent + 1, builder.tags))
        children.addAll(builder.children)
    }

    fun task(text: String, block: ItemBuilder.() -> Unit = {}) {
        val builder = ItemBuilder(indent + 1)
        builder.block()
        children.add(Task(text, indent + 1, builder.tags))
        children.addAll(builder.children)
    }

    fun note(text: String, block: ItemBuilder.() -> Unit = {}) {
        val builder = ItemBuilder(indent + 1)
        builder.block()
        children.add(Note(text, indent + 1, builder.tags))
        children.addAll(builder.children)
    }
}

@TaskPaperDsl
class TagBuilder {
    internal val values = mutableListOf<String>()

    fun value(v: String) {
        values.add(v)
    }
}

fun TaskPaper(block: TaskPaperBuilder.() -> Unit): TaskPaper {
    val builder = TaskPaperBuilder()
    builder.block()
    return builder.build()
}

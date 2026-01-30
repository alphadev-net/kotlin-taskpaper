package net.alphadev.taskpaper.export

import net.alphadev.taskpaper.format.*

fun TaskPaper.toTaskPaperString(): String = buildString {
    items.forEach { item ->
        append(item.toTaskPaperLine())
    }
}.trimEnd()

private fun Item.toTaskPaperLine(): String = buildString {
    repeat(indentLevel) { append('\t') }
    append(when (val item = this@toTaskPaperLine) {
        is Project -> item.toTaskPaperLine()
        is Task -> item.toTaskPaperLine()
        is Note -> text
    })

    tags.forEach { append(it.toTaskPaperLine()) }

    appendLine()
}

fun Project.toTaskPaperLine() = "$text:"

fun Task.toTaskPaperLine() = "- $text"

fun Map.Entry<String, String?>.toTaskPaperLine() = buildString {
    append(" @$key")
    value?.let { append("($value)") }
}

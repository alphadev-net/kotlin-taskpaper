package net.alphadev.taskpaper.import

import net.alphadev.taskpaper.format.TaskPaper
import net.alphadev.taskpaper.format.Task
import net.alphadev.taskpaper.format.Item
import net.alphadev.taskpaper.format.Project
import net.alphadev.taskpaper.format.Note
import net.alphadev.taskpaper.format.Tags

fun parseTaskPaperFromString(input: String): TaskPaper? = try {
    TaskPaper(
        input.lines()
            .mapIndexed { index, line -> index + 1 to line }
            .filter { (_, line) -> line.isNotEmpty() }
            .map { (lineNum, line) -> parseLine(line, lineNum) }
    )
} catch (ex: TaskPaperParseException) {
    null
}

private fun parseLine(line: String, lineNum: Int): Item {

    val indentLevel = line.takeWhile { it == '\t' }.length
    val content = line.trimStart('\t')

    val (text, tags) = extractTags(content, lineNum)

    return when {
        text.endsWith(':') -> Project(text.dropLast(1), indentLevel, tags)
        text.startsWith("- ") -> Task(text.drop(2), indentLevel, tags)
        else -> Note(text, indentLevel, tags)
    }
}

private fun extractTags(content: String, lineNum: Int): Pair<String, Tags> {
    var text = content

    val tags = buildMap {
        var i = 0
        while (i < text.length) {
            if (text[i] == '@') {
                val tagStart = i
                i++

                val nameStart = i
                while (i < text.length && (text[i].isLetterOrDigit() || text[i] == '_')) {
                    i++
                }

                if (i == nameStart) {
                    throw TaskPaperParseException(lineNum, content, "Empty tag name after '@'")
                }

                val tagName = text.substring(nameStart, i)
                val tagValues = mutableListOf<String>()

                if (i < text.length && text[i] == '(') {
                    val valueStart = i + 1
                    var depth = 1
                    i++

                    while (i < text.length && depth > 0) {
                        when (text[i]) {
                            '(' -> depth++
                            ')' -> depth--
                        }
                        i++
                    }

                    if (depth != 0) {
                        throw TaskPaperParseException(lineNum, content, "Unclosed parenthesis in tag value")
                    }

                    val valueContent = text.substring(valueStart, i - 1)
                    if (valueContent.isNotEmpty()) {
                        // Split by comma, handling nested parentheses
                        tagValues.addAll(splitTagValues(valueContent))
                    }
                }

                if (containsKey(tagName)) {
                    throw TaskPaperParseException(lineNum, content, "Duplicate tag '@$tagName'")
                }

                put(tagName, tagValues)
                text = text.substring(0, tagStart) + text.substring(i)
                i = tagStart
            } else {
                i++
            }
        }
    }

    return text.trim() to tags
}

private fun splitTagValues(content: String): List<String> {
    val values = mutableListOf<String>()
    var current = StringBuilder()
    var depth = 0

    for (char in content) {
        when {
            char == '(' -> {
                depth++
                current.append(char)
            }
            char == ')' -> {
                depth--
                current.append(char)
            }
            char == ',' && depth == 0 -> {
                values.add(current.toString().trim())
                current = StringBuilder()
            }
            else -> current.append(char)
        }
    }

    if (current.isNotEmpty()) {
        values.add(current.toString().trim())
    }

    return values
}

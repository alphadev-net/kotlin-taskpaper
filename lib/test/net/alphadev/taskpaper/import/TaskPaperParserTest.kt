package net.alphadev.taskpaper.import

import net.alphadev.taskpaper.format.Task
import net.alphadev.taskpaper.format.TaskPaper
import kotlin.test.*

class TaskParserParserTest {

    @Test
    fun emptyTagNameThrowsException() {
        val input = "- Task with empty tag @"

        val exception = assertFails { parseTaskPaperFromString(input, failOnError = true) }
        assertIs<TaskPaperParseException>(exception)

        assertContains(exception.message!!, "Empty tag name after '@'")
    }

    @Test
    fun unclosedParenthesisThrowsException() {
        val input = "- Task @due(2024-01-30"

        val exception = assertFails { parseTaskPaperFromString(input, failOnError = true) }
        assertIs<TaskPaperParseException>(exception)

        assertContains(exception.message!!, "Unclosed parenthesis in tag value")
    }

    @Test
    fun duplicateTagsThrowException() {
        val input = "- Task @priority(high) @priority(low)"

        val exception = assertFails { parseTaskPaperFromString(input, failOnError = true) }
        assertIs<TaskPaperParseException>(exception)

        assertContains(exception.message!!, "Duplicate tag")
    }

    @Test
    fun correctLineNumberOnMultilineInput() {
        val input = """
            Project:
            - Valid task
            - Invalid task @
            - Another task
        """.trimIndent()

        val result = parseTaskPaperFromString(input)
        assertNull(result)
    }

    @Test
    fun nestedParenthesesAreHandledCorrectly() {
        val input = "- Task @note(value with (nested) parens)"

        val taskPaper = parseTaskPaperFromString(input)
        assertIs<TaskPaper>(taskPaper)

        val task = taskPaper.items[0]
        assertIs<Task>(task)

        assertEquals("value with (nested) parens", task.tags["note"]?.first())
    }

    @Test
    fun parseTagWithMultipleValues() {
        val input = "- Task @people(Alice, Bob, Charlie)"

        val taskPaper = parseTaskPaperFromString(input)
        assertIs<TaskPaper>(taskPaper)

        val task = taskPaper.items[0] as Task
        assertEquals(listOf("Alice", "Bob", "Charlie"), task.tags["people"])
    }
}

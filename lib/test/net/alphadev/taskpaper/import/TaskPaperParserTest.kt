package net.alphadev.taskpaper.import

import net.alphadev.taskpaper.format.Task
import net.alphadev.taskpaper.format.TaskPaper
import kotlin.test.assertEquals
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class TaskParserParserTest {

    @Test
    fun emptyTagNameThrowsException() {
        val input = "- Task with empty tag @"

        val result = parseTaskPaperFromString(input)
        assertNull(result)
    }

    @Test
    fun unclosedParenthesisThrowsException() {
        val input = "- Task @due(2024-01-30"

        val result = parseTaskPaperFromString(input)
        assertNull(result)
    }

    @Test
    fun duplicateTagsThrowException() {
        val input = "- Task @priority(high) @priority(low)"

        val result = parseTaskPaperFromString(input)
        assertNull(result)
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

        assertEquals("value with (nested) parens", task.tags["note"])
    }
}

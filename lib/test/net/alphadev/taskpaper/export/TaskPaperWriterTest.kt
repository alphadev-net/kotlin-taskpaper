package net.alphadev.taskpaper.export

import net.alphadev.taskpaper.format.*
import net.alphadev.taskpaper.import.parseTaskPaperFromString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskPaperWriterTest {

    @Test
    fun serializeSimpleProject() {
        val taskPaper = TaskPaper(listOf(
            Project("My Project", 0)
        ))

        assertEquals("My Project:", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeSimpleTsk() {
        val taskPaper = TaskPaper(listOf(
            Task("Do something", 0)
        ))

        assertEquals("- Do something", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeSimpleNote() {
        val taskPaper = TaskPaper(listOf(
            Note("Just a note", 0)
        ))

        assertEquals("Just a note", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeIemWithTagWithoutValue() {
        val taskPaper = TaskPaper(listOf(
            Task("Important task", 0, tag("urgent"))
        ))

        assertEquals("- Important task @urgent", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeIemWithTagWithValue() {
        val taskPaper = TaskPaper(listOf(
            Task("Schedule meeting", 0, tag("due", "2024-06-20"))
        ))

        assertEquals("- Schedule meeting @due(2024-06-20)", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeIemWithMultipleTags() {
        val taskPaper = TaskPaper(listOf(
            Task("Review code", 0,
                 tag("priority", "high"),
                 tag("context", "work"),
                 tag("done")
            ))
        )

        val result = taskPaper.toTaskPaperString()
        assertTrue(result.startsWith("- Review code"))
        assertTrue(result.contains("@priority(high)"))
        assertTrue(result.contains("@context(work)"))
        assertTrue(result.contains("@done"))
    }

    @Test
    fun serializeIndentedItems() {
        val taskPaper = TaskPaper(listOf(
            Project("Project", 0),
            Task("Task 1", 1),
            Task("Task 2", 1),
            Note("Note under task 2", 2)
        ))

        val expected = """
            Project:
            	- Task 1
            	- Task 2
            		Note under task 2
        """.trimIndent()

        assertEquals(expected, taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeComplexDocument() {
        val taskPaper = TaskPaper(listOf(
            Project("Work", 0, tag("context", "office")),
            Task("Meeting prep", 1, tag("due", "2024-06-20")),
            Note("Bring laptop", 2),
            Task("Code review", 1, tag("priority", "high"), tag("done")),
            Project("Personal", 0),
            Task("Groceries", 1),
            Task("Gym", 1, tag("time", "6pm"))
        ))

        val expected = """
            Work: @context(office)
            	- Meeting prep @due(2024-06-20)
            		Bring laptop
            	- Code review @priority(high) @done
            Personal:
            	- Groceries
            	- Gym @time(6pm)
        """.trimIndent()

        assertEquals(expected, taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeAndParseRoundtrip() {
        val original = """
            Project: @context(test)
            	- Task 1 @priority(high)
            	- Task 2
            		Note here
        """.trimIndent()

        val parsed = parseTaskPaperFromString(original)
        val serialized = parsed?.toTaskPaperString()

        assertEquals(original, serialized)
    }

    @Test
    fun serializeEmptyTaskpaper() {
        val taskPaper = TaskPaper(emptyList())

        assertEquals("", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeTagWithParenthesesInValue() {
        val taskPaper = TaskPaper(listOf(
            Task("Task", 0, tag("note", "value (with parens)"))
        ))

        assertEquals("- Task @note(value (with parens))", taskPaper.toTaskPaperString())
    }
}

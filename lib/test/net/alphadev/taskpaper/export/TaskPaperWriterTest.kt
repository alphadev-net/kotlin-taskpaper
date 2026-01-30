package net.alphadev.taskpaper.export

import net.alphadev.taskpaper.format.*
import net.alphadev.taskpaper.import.parseTaskPaperFromString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaskPaperWriterTest {

    @Test
    fun serializeSimpleProject() {
        val taskPaper = TaskPaper {
            project("My Project")
        }

        assertEquals("My Project:", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeSimpleTsk() {
        val taskPaper = TaskPaper {
            task("Do something")
        }

        assertEquals("- Do something", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeSimpleNote() {
        val taskPaper = TaskPaper {
            note("Just a note")
        }

        assertEquals("Just a note", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeIemWithTagWithoutValue() {
        val taskPaper = TaskPaper {
            task("Important task") {
                tag("urgent")
            }
        }

        assertEquals("- Important task @urgent", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeIemWithTagWithValue() {
        val taskPaper = TaskPaper {
            task("Schedule meeting") {
                tag("due") {
                    value("2024-06-20")
                }
            }
        }

        assertEquals("- Schedule meeting @due(2024-06-20)", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeIemWithMultipleTags() {
        val taskPaper = TaskPaper {
            task("Review code") {
                tag("priority") { value( "high") }
                tag("context") { value("work") }
                tag("done")
            }
        }

        val result = taskPaper.toTaskPaperString()
        assertTrue(result.startsWith("- Review code"))
        assertTrue(result.contains("@priority(high)"))
        assertTrue(result.contains("@context(work)"))
        assertTrue(result.contains("@done"))
    }

    @Test
    fun serializeIndentedItems() {
        val taskPaper = TaskPaper {
            project("Project") {
                task("Task 1")
                task("Task 2") {
                    note("Note under task 2")
                }
            }
        }

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
        val taskPaper = TaskPaper {
            project("Work") {
                tag("context") {
                    value("office")
                }
                task("Meeting prep") {
                    tag("due") {
                        value("2024-06-20")
                    }
                    note("Bring laptop")
                }
                task("Code review") {
                    tag("priority") { value("high") }
                    tag("done")
                }
            }
            project("Personal") {
                task("Groceries")
                task("Gym") {
                    tag("time") {
                        value("6pm")
                    }
                }
            }
        }

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
        val taskPaper = TaskPaper {}

        assertEquals("", taskPaper.toTaskPaperString())
    }

    @Test
    fun serializeTagWithParenthesesInValue() {
        val taskPaper = TaskPaper {
            task("Task") {
                tag("note") {
                    value("value (with parens)")
                }
            }
        }

        assertEquals("- Task @note(value (with parens))", taskPaper.toTaskPaperString())
    }
}

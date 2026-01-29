package net.alphadev.taskpaper.import

class TaskPaperParseException(
    val line: Int,
    val content: String,
    message: String
) : Exception("Line $line: $message - '$content'")

package net.alphadev.taskpaper.import

public class TaskPaperParseException internal constructor(
    line: Int,
    content: String,
    message: String
) : Exception("Line $line: $message - '$content'")

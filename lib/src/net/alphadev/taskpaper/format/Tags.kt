package net.alphadev.taskpaper.format

typealias Tags = Map<String, List<String>>

fun tag(name: String, vararg values: String): Pair<String, List<String>> =
    name to values.toList()
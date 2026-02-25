package com.devtech.accahelps.util

object RangeParser {
    fun expand(input: String): List<Int> {
        if (input.isBlank()) return emptyList()

        val ranges = input.split(",")
            .map { it.trim() }
            .flatMap { segment ->
                val rangeParts = segment.split(Regex("[-–—]| to "))
                    .mapNotNull { it.filter { char -> char.isDigit() }.toIntOrNull() }

                when (rangeParts.size) {
                    2 -> (rangeParts[0]..rangeParts[1]).toList()
                    1 -> listOf(rangeParts[0])
                    else -> emptyList()
                }
            }.distinct()
        if (ranges.isEmpty()) {
            println("Error parsing: $input")
        }
        return ranges
    }

    fun isImportant(number: Int, importantRange: String): Boolean {
        if (importantRange.equals("All", ignoreCase = true)) return true
        return expand(importantRange).contains(number)
    }
}
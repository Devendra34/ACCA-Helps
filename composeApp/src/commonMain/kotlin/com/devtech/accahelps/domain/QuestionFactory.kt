package com.devtech.accahelps.domain

import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source

object QuestionFactory {

    fun newQuestions(
        source: Source,
        section: Section,
        inputRange: String,
        type: String = "",
        studyHubChapter: String = "",
        isImportant: Boolean = false
    ): List<Question> {
        val questionsToAdd = mutableListOf<Question>()

        // 1. Split by comma to get discrete parts (e.g., "1-5, 10, 12-14")
        val segments = inputRange.split(",")

        for (segment in segments) {
            val trimmedSegment = segment.trim()
            if (trimmedSegment.isEmpty()) continue

            // 2. Check if the segment is a range (contains -, –, —, or " to ")
            val rangeParts = trimmedSegment.split(Regex("[-–—]| to "))
                .map { it.trim().filter { char -> char.isDigit() } }
                .filter { it.isNotEmpty() }

            if (rangeParts.size == 2) {
                // It's a range: e.g., "1-5"
                val start = rangeParts[0].toIntOrNull() ?: 0
                val end = rangeParts[1].toIntOrNull() ?: 0

                // Handle both ascending (1-5) and descending (5-1) if needed
                val range = if (start <= end) start..end else start downTo end
                for (i in range) {
                    questionsToAdd.add(
                        newQuestion(
                            source,
                            i.toString(),
                            section,
                            type,
                            studyHubChapter,
                            isImportant
                        )
                    )
                }
            } else if (rangeParts.size == 1) {
                // It's a single number: e.g., "10"
                if (rangeParts[0].isBlank()) continue
                questionsToAdd.add(
                    // Pass isImportant to your helper
                    newQuestion(source, rangeParts[0], section, type, studyHubChapter, isImportant)
                )
            }
        }
        return questionsToAdd.distinctBy { it.fullPath }
    }

    fun newQuestion(
        source: Source,
        number: String,
        section: Section,
        type: String,
        chapter: String,
        isImportant: Boolean
    ): Question {
        return when (source) {
            Source.Kaplan -> Question.Kaplan(number, section, isImportant)
            Source.Bpp -> Question.Bpp(number, section, isImportant)
            Source.StudyHub -> Question.StudyHub(section, type, chapter, number, isImportant)
        }
    }
}
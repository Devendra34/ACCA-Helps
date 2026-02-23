package com.devtech.accahelps.domain

import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.util.TimeFormatter

object QuestionsSelector {

    fun maxQuestionsFor(
        section: Section,
    ): Int {
        return when (section) {
            Section.A -> 15
            Section.B -> 3
            Section.C -> 2
        }
    }

    fun formatQuestions(questionsBySection: Map<Section, List<Question>>): String {
        val sb = StringBuilder()
        sb.append("--- GENERATED QUESTION PAPER ---\n\n")

        questionsBySection.keys.sorted().forEach { section ->
            val questions = questionsBySection[section] ?: emptyList()
            if (questions.isNotEmpty()) {
                sb.append("${section.label}\n")
                sb.append("=".repeat(section.label.length + 9)).append("\n")

                questions.forEachIndexed { index, question ->
                    sb.append("${index + 1}. ${question.fullPath}\n")
                }
                sb.append("\n")
            }
        }

        sb.append("Generated on: ${TimeFormatter.formatCurrentTime()}") // Optional helper
        return sb.toString()
    }
}

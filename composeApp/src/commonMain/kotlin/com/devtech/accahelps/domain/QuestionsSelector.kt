package com.devtech.accahelps.domain

import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import com.devtech.accahelps.model.questionFor
import com.devtech.accahelps.util.TimeFormatter

object QuestionsSelector {

    fun selectedQuestions(
        section: Section,
        sources: List<Source>,
        questions: List<Question>,
        limit: Int,
    ): List<Question> {

        val pool = sources.flatMap { questions.questionFor(section, it) }

        val importantOnes = pool.filter { it.isImportant }.shuffled()
        val normalOnes = pool.filter { !it.isImportant }.shuffled()

        val selected = mutableListOf<Question>()

        if (importantOnes.isNotEmpty()) {
            selected.add(importantOnes.first())

            val remainingPool = (importantOnes.drop(1) + normalOnes).shuffled()
            selected.addAll(remainingPool.take(limit - 1))
        } else {
            selected.addAll(pool.shuffled().take(limit))
        }
        return selected
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

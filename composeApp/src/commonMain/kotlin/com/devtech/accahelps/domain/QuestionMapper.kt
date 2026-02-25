package com.devtech.accahelps.domain

import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import com.devtech.accahelps.util.RangeParser

data class QuestionRow(
    val source: Source,
    val section: Section,
    val type: String,
    val chapterRange: String,
    val questionRange: String,
    val importantRange: String
)

class QuestionMapper {

    fun mapRowToQuestions(row: QuestionRow): List<Question> {
        val source = row.source
        val section = row.section

        val chapters = if (source == Source.StudyHub)
            RangeParser.expand(row.chapterRange) else listOf(0)
        val questions = RangeParser.expand(row.questionRange)
        if (source == Source.StudyHub && section == Section.B) {
            println("Row: $chapters to $questions")
        }
        if (chapters.isEmpty() || questions.isEmpty()) {
            println("Error parsing: $row")
        }

        return chapters.flatMap { ch ->
            questions.map { q ->
                val isImp = RangeParser.isImportant(q, row.importantRange)
                when (source) {
                    Source.StudyHub -> Question.StudyHub(
                        section = section,
                        questionType = row.type,
                        chapterNumber = ch.toString(),
                        questionNumber = q.toString(),
                        isImportant = isImp
                    )

                    Source.Kaplan -> Question.Kaplan(q.toString(), section, isImp)
                    Source.Bpp -> Question.Bpp(q.toString(), section, isImp)
                }
            }
        }.also {
            if (source == Source.StudyHub && section == Section.B) {
                println("Q: $it")
            }
        }
    }
}

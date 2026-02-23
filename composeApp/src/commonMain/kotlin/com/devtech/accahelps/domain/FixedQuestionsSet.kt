package com.devtech.accahelps.domain

import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source

object FixedQuestionsSet {

    fun getInitialAssetQuestions(): List<Question> {
        val allQuestions = mutableListOf<Question>()

        // --- KAPLAN ---
        allQuestions.addAll(QuestionFactory.newQuestions(Source.Kaplan, Section.A, "1-229", "", ""))
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                Source.Kaplan,
                Section.B,
                "230-262",
                "",
                ""
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                Source.Kaplan,
                Section.C,
                "263-314",
                "",
                ""
            )
        )

        // --- BPP ---
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                Source.Bpp,
                Section.A,
                "1-30, 32-61, 97-131, 171-200, 264-293",
                "",
                ""
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                Source.Bpp,
                Section.B,
                "31, 62, 67, 72, 77, 82, 87, 92, 132, 137, 142, 147, 152, 157, 201, 206, 211, 216, 221, 226, 231, 236, 241, 246, 294, 299, 304, 309, 314, 319, 324, 329",
                "",
                ""
            )
        )
        allQuestions.addAll(
            QuestionFactory.newQuestions(
                Source.Bpp,
                Section.C,
                "162-170, 251-263, 334-350",
                "",
                ""
            )
        )

        // --- STUDY HUB ---
        // 1. Quizzes (Ch 1-18, 5 questions each)
        for (ch in 1..18) {
            allQuestions.addAll(
                QuestionFactory.newQuestions(
                    Source.StudyHub,
                    Section.A,
                    "1-5",
                    "Quiz",
                    ch.toString()
                )
            )
        }

        // 2. OT Revision Questions (Chapter-specific counts)
        val otRevisionData = mapOf(
            1 to 11, 2 to 9, 3 to 33, 4 to 16, 5 to 15, 6 to 11,
            7 to 11, 8 to 7, 9 to 10, 10 to 18, 11 to 6, 12 to 12,
            13 to 14, 14 to 7, 15 to 8, 16 to 6, 17 to 14, 18 to 13
        )

        otRevisionData.forEach { (chapter, count) ->
            allQuestions.addAll(
                QuestionFactory.newQuestions(
                    Source.StudyHub,
                    Section.A,
                    "1-$count",
                    "OT Revision",
                    chapter.toString()
                )
            )
        }

        return allQuestions
    }
}
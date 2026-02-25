package com.devtech.accahelps.data.model

import com.devtech.accahelps.QuestionEntity
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Source

fun QuestionEntity.toDomainModel(): Question {
    val isImp = isImportant == true
    return when (source) {
        Source.StudyHub -> Question.StudyHub(
            section = section,
            questionType = questionType.orEmpty(),
            chapterNumber = chapterNumber.orEmpty(),
            questionNumber = questionNumber,
            isImportant = isImp
        )

        Source.Kaplan -> Question.Kaplan(questionNumber, section, isImp)
        Source.Bpp -> Question.Bpp(questionNumber, section, isImp)
    }

}
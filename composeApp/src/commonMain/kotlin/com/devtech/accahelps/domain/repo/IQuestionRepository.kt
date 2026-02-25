package com.devtech.accahelps.domain.repo

import com.devtech.accahelps.data.source.FixedQuestionsSet
import com.devtech.accahelps.domain.QuestionsSelector
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

interface IQuestionRepository {
    fun getQuestionsFlow(): Flow<List<Question>>

    suspend fun addQuestions(questions: List<Question>)

    suspend fun deleteQuestion(question: Question)

    fun getQuestionsFlow(section: Section, source: Source): Flow<List<Question>> {
        return getQuestionsFlow().map { it.filter { q -> q.source == source && q.section == section } }
    }

    suspend fun generateRandom(section: Section, sources: List<Source>): List<Question> {
        val questions = getQuestionsFlow().first { it.isNotEmpty() }
        return QuestionsSelector.selectedQuestions(section, sources, questions, limitFor(section))
    }


    fun limitFor(section: Section): Int {
        return when (section) {
            Section.A -> 15
            Section.B -> 3
            Section.C -> 2
        }
    }

    val settingsFlow: Flow<AppSettings>

    suspend fun saveSettings(settings: AppSettings)

    fun canEdit() = true

}

@Serializable
data class AppState(
    val questions: List<Question> = FixedQuestionsSet.getInitialAssetQuestions(),
    val settings: AppSettings = AppSettings()
)
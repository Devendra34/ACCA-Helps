package com.devtech.accahelps.data.source

import com.devtech.accahelps.domain.QuestionsSelector
import com.devtech.accahelps.domain.store.AppDbStore
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map


class InMemoryDbStore() : AppDbStore {
    private val questions = MutableStateFlow<Set<Question>>(emptySet())
    private var appSettings = MutableStateFlow(AppSettings())

    override fun getQuestionsFlow(): Flow<List<Question>> {
        return questions.map { it.toList() }
    }

    override fun getQuestionsFlow(section: Section, source: Source): Flow<List<Question>> {
        return getQuestionsFlow().map { it.filter { q -> q.source == source && q.section == section } }
    }

    override fun settingsFlow(): Flow<AppSettings> {
        return appSettings
    }

    override suspend fun clearAndInsertQuestions(questions: List<Question>) {
        val newQuestions = hashSetOf<Question>()
        newQuestions.addAll(questions)
        this.questions.value = newQuestions.toSet()
    }

    override suspend fun insertOrUpdateQuestions(questions: List<Question>) {
        val newQuestions = HashSet(this.questions.value)
        newQuestions.addAll(questions)
        this.questions.value = newQuestions.toSet()
    }

    override suspend fun deleteQuestion(question: Question) {
        val newQuestions = HashSet(this.questions.value)
        newQuestions.remove(question)
        this.questions.value = newQuestions.toSet()
    }

    override suspend fun updateAppSettings(appSettings: AppSettings) {
        this.appSettings.value = appSettings
    }

    override suspend fun getRandom(
        section: Section,
        sources: List<Source>,
        limit: Int
    ): List<Question> {
        return QuestionsSelector.selectedQuestions(
            section,
            sources,
            questions.value.toList(),
            limit
        )
    }

    override suspend fun hasData(): Boolean {
        return questions.value.isNotEmpty()
    }
}
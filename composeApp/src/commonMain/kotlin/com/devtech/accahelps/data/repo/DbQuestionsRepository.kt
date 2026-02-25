package com.devtech.accahelps.data.repo

import com.devtech.accahelps.domain.repo.IQuestionRepository
import com.devtech.accahelps.domain.store.AppDbStore
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.flow.Flow

class DbQuestionsRepository(
    private val appDbStore: AppDbStore
) : IQuestionRepository {

    override fun getQuestionsFlow(): Flow<List<Question>> {
        return appDbStore.getQuestionsFlow()
    }

    override fun getQuestionsFlow(section: Section, source: Source): Flow<List<Question>> {
        return appDbStore.getQuestionsFlow(section, source)
    }

    override val settingsFlow: Flow<AppSettings>
        get() = appDbStore.settingsFlow()

    override suspend fun generateRandom(section: Section, sources: List<Source>): List<Question> {
        val limit = limitFor(section)
        return appDbStore.getRandom(section, sources, limit)
    }

    override suspend fun addQuestions(questions: List<Question>) {
        appDbStore.insertOrUpdateQuestions(questions)
    }

    override suspend fun deleteQuestion(question: Question) {
        appDbStore.deleteQuestion(question)
    }

    override suspend fun saveSettings(settings: AppSettings) {
        appDbStore.updateAppSettings(settings)
    }

    override fun canEdit(): Boolean {
        return false
    }
}
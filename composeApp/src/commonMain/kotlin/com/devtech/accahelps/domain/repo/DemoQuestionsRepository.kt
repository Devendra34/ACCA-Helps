package com.devtech.accahelps.domain.repo

import com.devtech.accahelps.domain.FixedQuestionsSet
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DemoQuestionsRepository : IQuestionRepository {
    private val questions = MutableStateFlow(
        FixedQuestionsSet.getInitialAssetQuestions()
    )

    private val appSettings = MutableStateFlow(
        AppSettings()
    )

    override fun getQuestionsFlow(): Flow<List<Question>> = questions.asStateFlow()

    override val settingsFlow: Flow<AppSettings> = appSettings.asStateFlow()

    override suspend fun updateQuestions(transform: (List<Question>) -> List<Question>) {
        questions.update { transform(it) }
    }

    override suspend fun saveSettings(settings: AppSettings) {
        appSettings.value = settings
    }
}
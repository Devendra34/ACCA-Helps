package com.devtech.accahelps.domain.repo

import com.devtech.accahelps.domain.FixedQuestionsSet
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

interface IQuestionRepository {
    fun getQuestionsFlow(): Flow<List<Question>>

    val settingsFlow: Flow<AppSettings>
    suspend fun updateQuestions(transform: (List<Question>) -> List<Question>)

    suspend fun saveSettings(settings: AppSettings)

}

@Serializable
data class AppState(
    val questions: List<Question> = FixedQuestionsSet.getInitialAssetQuestions(),
    val settings: AppSettings = AppSettings()
)
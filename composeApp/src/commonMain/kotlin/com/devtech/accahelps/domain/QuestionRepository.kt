package com.devtech.accahelps.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

interface IQuestionRepository {
    fun getQuestionsFlow(): Flow<List<Question>>

    val settingsFlow: Flow<AppSettings>
    suspend fun updateQuestions(transform: (List<Question>) -> List<Question>)

    suspend fun saveSettings(settings: AppSettings)
}

class QuestionRepository(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) : IQuestionRepository {

    // 1. Observe questions as a Flow
    override fun getQuestionsFlow(): Flow<List<Question>> = dataStore.data
        .map { prefs ->
            val jsonString = prefs[QUESTIONS_KEY]
            if (jsonString == null) {
                // FALLBACK: If storage is empty, load from Assets
                val initial = loadFromAssets()
                updateQuestions { initial }
                initial
            } else {
                json.decodeFromString<List<Question>>(jsonString)
            }
        }

    // 2. Add / Edit / Remove (Saves instantly)
    override suspend fun updateQuestions(transform: (List<Question>) -> List<Question>) {
        dataStore.edit { prefs ->
            val currentJson = prefs[QUESTIONS_KEY]
            val currentList =
                currentJson?.let { json.decodeFromString<List<Question>>(it) } ?: emptyList()
            val newList = transform(currentList)
            prefs[QUESTIONS_KEY] = json.encodeToString(newList)
        }
    }

    private fun loadFromAssets(): List<Question> {
        return FixedQuestionsSet.getInitialAssetQuestions()
    }

    override val settingsFlow: Flow<AppSettings> = dataStore.data.map { prefs ->
        val jsonString = prefs[SETTINGS_KEY]
        if (jsonString == null) AppSettings()
        else json.decodeFromString<AppSettings>(jsonString)
    }

    override suspend fun saveSettings(settings: AppSettings) {
        dataStore.edit { prefs ->
            prefs[SETTINGS_KEY] = json.encodeToString(settings)
        }
    }

    companion object {
        private val QUESTIONS_KEY = stringPreferencesKey("saved_questions")
        private val SETTINGS_KEY = stringPreferencesKey("app_settings")

    }
}

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

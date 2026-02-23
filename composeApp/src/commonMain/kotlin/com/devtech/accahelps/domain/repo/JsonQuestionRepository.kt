package com.devtech.accahelps.domain.repo

import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class JsonQuestionRepository(
    private val file: File,
    private val json: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        classDiscriminator = "type"
        encodeDefaults = true
    }
) : IQuestionRepository {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _state = MutableStateFlow(loadState())
    private val state: StateFlow<AppState> = _state.asStateFlow()

    override fun getQuestionsFlow(): Flow<List<Question>> =
        state.map { it.questions }

    override val settingsFlow: Flow<AppSettings> =
        state.map { it.settings }

    override suspend fun updateQuestions(
        transform: (List<Question>) -> List<Question>
    ) {
        val updated = _state.value.copy(
            questions = transform(_state.value.questions)
        )
        persist(updated)
    }

    override suspend fun saveSettings(settings: AppSettings) {
        val updated = _state.value.copy(settings = settings)
        persist(updated)
    }

    private fun loadState(): AppState {
        return if (file.exists()) {
            try {
                json.decodeFromString<AppState>(file.readText())
            } catch (_: Exception) {
                AppState().also { persistAsync(it) }
            }
        } else {
            AppState().also { persistAsync(it) }
        }
    }

    private fun persistAsync(newState: AppState) {
        scope.launch {
            persist(newState)
        }
    }

    private suspend fun persist(newState: AppState) {
        withContext(Dispatchers.IO) {
            file.writeText(json.encodeToString(newState))
        }
        _state.value = newState
    }
}
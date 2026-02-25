package com.devtech.accahelps

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.devtech.accahelps.domain.QuestionFactory
import com.devtech.accahelps.domain.QuestionRangeInput
import com.devtech.accahelps.domain.repo.IQuestionRepository
import com.devtech.accahelps.domain.repo.SyncRepository
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.SectionSelection
import com.devtech.accahelps.model.SectionState
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.reflect.KClass


data class GeneratorUiState(
    val isPopupVisible: Boolean = false,
    val selectedQuestions: Map<Section, List<Question>> = emptyMap(),
    val isLoading: Boolean = false
)

class MainViewModel(
    private val repository: IQuestionRepository,
    private val syncRepository: SyncRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GeneratorUiState())
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    val canEditQuestions = repository.canEdit()

    val viewQuestionsFor = MutableStateFlow<Pair<Source, Section>?>(null)

    val addQuestionsFor = MutableStateFlow<Pair<Source, Section>?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val viewQuestions = viewQuestionsFor.flatMapLatest {
        if (it == null) return@flatMapLatest flowOf(emptyList())
        repository.getQuestionsFlow(it.second, it.first)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val sections = listOf(
        SectionState(Section.A),
        SectionState(Section.B),
        SectionState(Section.C),
    )

    init {
        viewModelScope.launch {
            loadAndObserveSelections()
        }
    }

    private suspend fun loadAndObserveSelections() {
        val savedSettings = repository.settingsFlow.first()

        savedSettings.sectionSelections.forEach { saved ->
            sections.find { it.section == saved.section }?.let { state ->
                state.isEnabled.value = saved.isEnabled
                state.sourcesState.forEach { sourceState ->
                    sourceState.isSelected.value =
                        saved.selectedSources.contains(sourceState.source)
                }
            }
        }

        snapshotFlow {
            sections.map {
                SectionSelection(
                    it.section,
                    it.isEnabled.value,
                    it.sourcesState.filter { s -> s.isSelected.value }.map { s -> s.source }
                        .toHashSet()
                )
            }
        }.collect { currentSelections ->
            repository.saveSettings(AppSettings(sectionSelections = currentSelections))
        }
    }

    fun generateQuestions() {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(isLoading = true) }

            val results = sections.filter { it.isEnabled.value }.associate { selectedSection ->
                val selectedSources =
                    selectedSection.sourcesState.filter { sourceState -> sourceState.isSelected.value }
                        .map { it.source }
                val questions = repository.generateRandom(selectedSection.section, selectedSources)
                selectedSection.section to questions
            }

            _uiState.update {
                it.copy(
                    selectedQuestions = results,
                    isPopupVisible = true,
                    isLoading = false
                )
            }
        }
    }

    fun dismissPopup() {
        _uiState.update { it.copy(isPopupVisible = false) }
    }

    fun addQuestionRange(
        input: QuestionRangeInput,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newOnes = QuestionFactory.newQuestions(input)
            repository.addQuestions(newOnes)
        }
    }

    fun removeQuestion(question: Question) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteQuestion(question)
        }
    }

    fun onRequestSync() {
        viewModelScope.launch { requestSync() }
    }

    fun ensureDataIsLoaded() {
        viewModelScope.launch {
            val alreadyHasData = syncRepository.hasData()
            if (!alreadyHasData) {
                println("Initial launch detected: Fetching data from Sheet...")
                requestSync()
            } else {
                println("Data already present. Skipping initial sync.")
            }
        }
    }

    private suspend fun requestSync() {
        _isSyncing.value = true
        try {
            syncRepository.syncFromSheet()
        } catch (e: Exception) {
            println("Error $e")
        }
        _isSyncing.value = false
    }
}


class MainViewModelFactory(
    private val repository: IQuestionRepository,
    private val syncRepository: SyncRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == MainViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, syncRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

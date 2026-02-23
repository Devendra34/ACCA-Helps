package com.devtech.accahelps

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.devtech.accahelps.domain.QuestionFactory
import com.devtech.accahelps.domain.QuestionsSelector
import com.devtech.accahelps.domain.repo.IQuestionRepository
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.SectionSelection
import com.devtech.accahelps.model.SectionState
import com.devtech.accahelps.model.Source
import com.devtech.accahelps.model.questionFor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
    private val repository: IQuestionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(GeneratorUiState())
    val uiState: StateFlow<GeneratorUiState> = _uiState.asStateFlow()


    val addedQuestions = repository.getQuestionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun questionsForFlow(source: Source, section: Section) = addedQuestions.map { questions ->
        questions.questionFor(section, source)
    }

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
                    it.sourcesState.filter { s -> s.isSelected.value }.map { s -> s.source }.toSet()
                )
            }
        }.collect { currentSelections ->
            repository.saveSettings(AppSettings(sectionSelections = currentSelections))
        }
    }

    fun generateQuestions() {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(isLoading = true) }
            val questions = addedQuestions.first { it.isNotEmpty() }

            // Perform randomization
            val results = sections.filter { it.isEnabled.value }.associate { sectionState ->
                val selected = QuestionsSelector.selectedQuestions(sectionState, questions)

                sectionState.section to selected.toList()
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
        source: Source,
        section: Section,
        inputRange: String,
        type: String = "",
        studyHubChapter: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newOnes = QuestionFactory.newQuestions(
                source, section, inputRange, type, studyHubChapter
            )
            repository.updateQuestions { current -> current + newOnes }
        }
    }

    fun removeQuestion(question: Question) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateQuestions { current -> current.filter { it != question } }
        }
    }
}


class MainViewModelFactory(
    private val repository: IQuestionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == MainViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

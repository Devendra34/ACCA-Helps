package com.devtech.accahelps

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devtech.accahelps.domain.repo.DemoQuestionsRepository
import com.devtech.accahelps.domain.repo.IQuestionRepository
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import com.devtech.accahelps.ui.AddQuestionDialog
import com.devtech.accahelps.ui.ResultDialog
import com.devtech.accahelps.ui.SectionSelectorUI
import com.devtech.accahelps.ui.ViewQuestionsDialog
import com.devtech.accahelps.ui.theme.AppTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun QuestionPickerApp(
    repository: IQuestionRepository = DemoQuestionsRepository()
) {
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(repository)
    )
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    AppTheme {
        Scaffold(
            topBar = { AppTopBar(scrollBehavior) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.generateQuestions() },
                    icon = { Icon(Icons.Default.PlayArrow, null) },
                    text = { Text("Generate") }
                )
            }
        ) { padding ->
            var selectedSectionSource by remember { mutableStateOf<Pair<Section, Source>?>(null) }
            var viewForSectionSource by remember { mutableStateOf<Pair<Section, Source>?>(null) }
            SectionSelectorUI(
                modifier = Modifier
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                sections = viewModel.sections,
                onEditSource = { section, source ->
                    selectedSectionSource = section to source
                },
                onViewSource = { section, source ->
                    viewForSectionSource = section to source
                }
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            if (uiState.isPopupVisible) {
                ResultDialog(
                    questionsBySection = uiState.selectedQuestions,
                    onDismiss = { viewModel.dismissPopup() }
                )
            }
            selectedSectionSource?.let {
                AddQuestionDialog(
                    section = it.first,
                    selectedSource = it.second,
                    onConfirm = { qRange, type, chapter ->
                        viewModel.addQuestionRange(it.second, it.first, qRange, type, chapter)
                        selectedSectionSource = null
                    },
                    onDismiss = {
                        selectedSectionSource = null
                    }
                )
            }
            viewForSectionSource?.let {
                ViewQuestionsDialog(
                    source = it.second,
                    section = it.first,
                    viewModel = viewModel,
                    onDismiss = { viewForSectionSource = null },
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    CenterAlignedTopAppBar(
        title = { Text("Question Picker") },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        )
    )
}
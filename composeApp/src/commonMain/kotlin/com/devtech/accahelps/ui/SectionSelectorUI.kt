package com.devtech.accahelps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.SectionState
import com.devtech.accahelps.model.Source


@Composable
fun SectionSelectorUI(
    modifier: Modifier = Modifier,
    sections: List<SectionState>,
    onEditSource: (Section, Source) -> Unit,
    onViewSource: (Section, Source) -> Unit,
) {

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 300.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(sections) { section ->
            SectionCard(
                section = section,
                onEditSource = { onEditSource(section.section, it) },
                onViewSource = { onViewSource(section.section, it) },
            )
        }
    }
}
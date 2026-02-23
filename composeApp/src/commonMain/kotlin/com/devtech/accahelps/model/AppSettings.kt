package com.devtech.accahelps.model

import kotlinx.serialization.Serializable

@Serializable
data class SectionSelection(
    val section: Section,
    val isEnabled: Boolean,
    val selectedSources: Set<Source>
)

@Serializable
data class AppSettings(
    val sectionSelections: List<SectionSelection> = emptyList()
)
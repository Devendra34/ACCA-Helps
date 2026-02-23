package com.devtech.accahelps.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
sealed class Question(
    val source: Source,
) {
    abstract val section: Section

    abstract val fullPath: String

    abstract val sourcePath: String

    abstract val isImportant: Boolean // New Property

    @Serializable
    @SerialName("Kaplan")
    data class Kaplan(
        val number: String,
        override val section: Section,
        override val isImportant: Boolean = false
    ) : Question(source = Source.Kaplan) {
        override val fullPath: String get() = "${source.label}: Q$number${if (isImportant) " ⭐" else ""}"

        override val sourcePath: String get() = "Q$number${if (isImportant) " ⭐" else ""}"
    }

    @Serializable
    @SerialName("Bpp")
    data class Bpp(
        val number: String,
        override val section: Section,
        override val isImportant: Boolean = false
    ) : Question(source = Source.Bpp) {
        override val fullPath: String get() = "${source.label}: Q$number${if (isImportant) " ⭐" else ""}"

        override val sourcePath: String get() = "Q$number${if (isImportant) " ⭐" else ""}"
    }

    @Serializable
    @SerialName("StudyHub")
    data class StudyHub(
        override val section: Section,
        val questionType: String,
        val chapterNumber: String,
        val questionNumber: String,
        override val isImportant: Boolean = false
    ) : Question(source = Source.StudyHub) {
        override val fullPath: String
            get() =
                "${source.label}: $questionType: CH-$chapterNumber, Q-$questionNumber${if (isImportant) " ⭐" else ""}"

        override val sourcePath: String
            get() =
                "$questionType: CH-$chapterNumber, Q-$questionNumber${if (isImportant) " ⭐" else ""}"
    }
}

enum class Source(val label: String) {
    Kaplan("Kaplan"),
    Bpp("BPP"),
    StudyHub("Study Hub")
}


fun List<Question>.questionFor(
    section: Section,
    source: Source
): List<Question> = filter { q -> q.section == section && q.source == source }
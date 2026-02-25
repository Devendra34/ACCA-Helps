package com.devtech.accahelps.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
sealed class Question(
    val source: Source,
) {
    abstract val section: Section

    abstract val id: String

    abstract val fullPath: String

    abstract val sourcePath: String

    abstract val isImportant: Boolean

    abstract val questionNumber: String

    @Serializable
    @SerialName("Kaplan")
    data class Kaplan(
        override val questionNumber: String,
        override val section: Section,
        override val isImportant: Boolean = false
    ) : Question(source = Source.Kaplan) {
        override val id: String = toString()
        override val fullPath: String get() = "${source.label}: Q$questionNumber${if (isImportant) " ⭐" else ""}"

        override val sourcePath: String get() = "Q$questionNumber${if (isImportant) " ⭐" else ""}"
    }

    @Serializable
    @SerialName("Bpp")
    data class Bpp(
        override val questionNumber: String,
        override val section: Section,
        override val isImportant: Boolean = false
    ) : Question(source = Source.Bpp) {
        override val id: String = toString()

        override val fullPath: String get() = "${source.label}: Q$questionNumber${if (isImportant) " ⭐" else ""}"

        override val sourcePath: String get() = "Q$questionNumber${if (isImportant) " ⭐" else ""}"
    }

    @Serializable
    @SerialName("StudyHub")
    data class StudyHub(
        override val section: Section,
        val questionType: String,
        val chapterNumber: String,
        override val questionNumber: String,
        override val isImportant: Boolean = false
    ) : Question(source = Source.StudyHub) {
        override val id: String = toString()

        override val fullPath: String
            get() =
                "${source.label}: $questionType: CH-$chapterNumber, Q-$questionNumber${if (isImportant) " ⭐" else ""}"

        override val sourcePath: String
            get() =
                "$questionType: CH-$chapterNumber, Q-$questionNumber${if (isImportant) " ⭐" else ""}"
    }
}


fun List<Question>.questionFor(
    section: Section,
    source: Source
): List<Question> = filter { q -> q.section == section && q.source == source }
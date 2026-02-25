package com.devtech.accahelps.data.source

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.devtech.accahelps.AppDatabase
import com.devtech.accahelps.QuestionEntity
import com.devtech.accahelps.SectionSelectionEntity
import com.devtech.accahelps.data.model.toDomainModel
import com.devtech.accahelps.domain.store.AppDbStore
import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.SectionSelection
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppDbHelper(private val database: AppDatabase) : AppDbStore {

    val queries get() = database.appDatabaseQueries
    private val context = Dispatchers.IO

    override fun getQuestionsFlow(): Flow<List<Question>> {
        return queries.selectAll().mapToDomainFlow()
    }

    override fun getQuestionsFlow(section: Section, source: Source): Flow<List<Question>> {
        return queries.selectAllForSectionSouce(section, source).mapToDomainFlow()
    }

    override fun settingsFlow(): Flow<AppSettings> = queries.getSelections()
        .asFlow()
        .mapToList(context)
        .map { selectionEntities ->
            AppSettings(selectionEntities.map {
                SectionSelection(it.section, it.isEnabled, it.selectedSources)
            })
        }

    override suspend fun clearAndInsertQuestions(questions: List<Question>) {
        withContext(context) {
            database.transaction {
                queries.clearAll()
                questions.distinctBy { it.id }.forEach { q ->
                    queries.insertOrAddQuestion(q.toEntity())
                }
            }
        }
    }

    override suspend fun insertOrUpdateQuestions(questions: List<Question>) {
        withContext(context) {
            database.transaction {
                questions.distinctBy { it.id }.forEach { q ->
                    queries.insertOrAddQuestion(q.toEntity())
                }
            }
        }
    }

    override suspend fun deleteQuestion(question: Question) {
        withContext(context) {
            database.transaction {
                queries.deleteQuestion(question.toEntity().id)
            }
        }
    }

    override suspend fun getRandom(
        section: Section,
        sources: List<Source>,
        limit: Int
    ): List<Question> {
        val importantQuestions = queries.getRandomImportant(section, sources, 1)
            .executeAsList()

        val excludedIds = importantQuestions.map { it.id }
        val pendingLimit = limit - importantQuestions.size
        val otherQuestions = queries.getRandom(
            section, sources, excludedIds, pendingLimit.toLong()
        ).executeAsList()

        return (importantQuestions + otherQuestions).map { it.toDomainModel() }
    }

    override suspend fun updateAppSettings(appSettings: AppSettings) {
        withContext(context) {
            database.transaction {
                appSettings.sectionSelections.forEach {
                    queries.updateSectionSelection(
                        SectionSelectionEntity(
                            it.section, it.isEnabled, it.selectedSources
                        )
                    )
                }
            }
        }
    }

    override suspend fun hasData(): Boolean {
        return queries.hasData().executeAsOneOrNull() ?: false
    }

    private fun Query<QuestionEntity>.mapToDomainFlow(): Flow<List<Question>> {
        return this.asFlow().mapToList(Dispatchers.Default)
            .map { it.map { e -> e.toDomainModel() } }
    }


    private fun Question.toEntity(): QuestionEntity {
        return QuestionEntity(
            id = id,
            source = source,
            section = section,
            isImportant = isImportant,
            questionType = (this as? Question.StudyHub)?.questionType,
            chapterNumber = (this as? Question.StudyHub)?.chapterNumber,
            questionNumber = questionNumber,
        )
    }
}
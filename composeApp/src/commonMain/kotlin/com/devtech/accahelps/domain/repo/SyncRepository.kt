package com.devtech.accahelps.domain.repo

import com.devtech.accahelps.domain.QuestionMapper
import com.devtech.accahelps.domain.QuestionRow
import com.devtech.accahelps.domain.store.AppDbStore
import com.devtech.accahelps.model.toSectionOrNull
import com.devtech.accahelps.model.toSourceOrNull
import com.devtech.accahelps.sheets.impl.SpreadsheetLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncRepository(
    private val spreadsheetLoader: SpreadsheetLoader = SpreadsheetLoader(),
    private val appDataHelper: AppDbStore,
    private val mapper: QuestionMapper = QuestionMapper(),
) {

    suspend fun syncFromSheet() = withContext(Dispatchers.IO) {
        val csvData = spreadsheetLoader.fetchTable()
        val rows = parseCsv(csvData)

        val allExpandedQuestions = rows.flatMap { mapper.mapRowToQuestions(it) }.filter {
            it.questionNumber.isNotBlank()
        }
        appDataHelper.clearAndInsertQuestions(allExpandedQuestions)
    }

    private fun parseCsv(data: List<List<String>>): List<QuestionRow> {
        return data.drop(1) // Drop header
            .mapNotNull { row ->
                val source = row.getOrNull(0)?.toSourceOrNull() ?: return@mapNotNull null
                val section = row.getOrNull(1)?.toSectionOrNull() ?: return@mapNotNull null
                QuestionRow(
                    source = source,
                    section = section,
                    type = row.getOrNull(2) ?: "",
                    chapterRange = row.getOrNull(3) ?: "",
                    questionRange = row.getOrNull(4) ?: "",
                    importantRange = row.getOrNull(5) ?: ""
                )
            }
    }

    suspend fun hasData(): Boolean {
        return appDataHelper.hasData()
    }
}

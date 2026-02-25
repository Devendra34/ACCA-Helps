package com.devtech.accahelps.domain.store

import com.devtech.accahelps.model.AppSettings
import com.devtech.accahelps.model.Question
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import kotlinx.coroutines.flow.Flow

interface AppDbStore {

    fun getQuestionsFlow(): Flow<List<Question>>

    fun getQuestionsFlow(section: Section, source: Source): Flow<List<Question>>

    fun settingsFlow(): Flow<AppSettings>

    suspend fun clearAndInsertQuestions(questions: List<Question>)

    suspend fun insertOrUpdateQuestions(questions: List<Question>)

    suspend fun deleteQuestion(question: Question)

    suspend fun updateAppSettings(appSettings: AppSettings)

    suspend fun getRandom(section: Section, sources: List<Source>, limit: Int): List<Question>

    suspend fun hasData(): Boolean
}
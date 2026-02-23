package com.devtech.accahelps

import androidx.compose.ui.platform.ClipEntry
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.devtech.accahelps.domain.QuestionRepository
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect fun String.toClipEntry(): ClipEntry

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
        produceFile = { producePath().toPath().toFile() }
    )

const val DATASTORE_FILE_NAME = "questions.preferences_pb"

class AppContainer(dataStore: DataStore<Preferences>) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        encodeDefaults = true
    }

    val repository = QuestionRepository(dataStore, json)
}
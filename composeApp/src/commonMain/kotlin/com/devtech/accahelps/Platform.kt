package com.devtech.accahelps

import androidx.compose.ui.platform.ClipEntry
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.devtech.accahelps.domain.repo.JsonQuestionRepository
import okio.Path.Companion.toPath
import java.io.File

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect fun String.toClipEntry(): ClipEntry

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
        produceFile = { producePath().toPath().toFile() }
    )

const val DATASTORE_FILE_NAME = "app_state.json"

class AppContainer(storeFolder: File) {
    val repository = JsonQuestionRepository(File(storeFolder, DATASTORE_FILE_NAME))
}
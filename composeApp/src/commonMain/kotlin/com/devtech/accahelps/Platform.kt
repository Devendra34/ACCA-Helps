package com.devtech.accahelps

import androidx.compose.ui.platform.ClipEntry
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect fun String.toClipEntry(): ClipEntry

expect fun String.urlEncode(): String
expect fun openUrl(url: String)

fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
        produceFile = { producePath().toPath().toFile() }
    )

const val DATASTORE_FILE_NAME = "app_state.json"

fun shareToWhatsApp(text: String) {
    // We must encode the text so characters like space, &, and \n don't break the URL
    val encodedText = text.urlEncode()
    openUrl("https://wa.me/?text=$encodedText")
}
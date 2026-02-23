package com.devtech.accahelps

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.awt.datatransfer.StringSelection

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

fun initDesktopDataStore(): DataStore<Preferences> = createDataStore {
    val dataDir = System.getProperty("user.home") + "/.accaHelp"
    java.io.File(dataDir).mkdirs()
    "$dataDir/$DATASTORE_FILE_NAME"
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toClipEntry(): ClipEntry {
    return ClipEntry(StringSelection(this))
}
package com.devtech.accahelps

import android.content.ClipData
import android.content.Context
import android.os.Build
import androidx.compose.ui.platform.ClipEntry
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

fun initAndroidDataStore(context: Context): DataStore<Preferences> = createDataStore {
    context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath
}

actual fun String.toClipEntry(): ClipEntry {
    val clipData = ClipData.newPlainText(this, this)
    return ClipEntry(clipData)
}
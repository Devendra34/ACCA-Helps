package com.devtech.accahelps

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection
import java.io.File

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

fun getStorePath(appName: String): File {
    val dataDir = System.getProperty("user.home") + "/.$appName"
    val folder = File(dataDir)
    folder.mkdirs()
    return folder
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toClipEntry(): ClipEntry {
    return ClipEntry(StringSelection(this))
}
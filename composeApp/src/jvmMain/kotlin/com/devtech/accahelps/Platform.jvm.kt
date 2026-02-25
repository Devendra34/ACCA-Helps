package com.devtech.accahelps

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.awt.Desktop
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

fun getStorePath(appName: String): File {
    val os = System.getProperty("os.name").lowercase()
    val userHome = System.getProperty("user.home")

    val dataDir = when {
        os.contains("win") -> {
            // Windows: C:\Users\Name\AppData\Roaming\appName
            val appData = System.getenv("APPDATA")
            if (appData != null) File(appData, appName)
            else File(userHome, "AppData/Roaming/$appName")
        }
        os.contains("mac") -> {
            // macOS: /Users/Name/Library/Application Support/appName
            File(userHome, "Library/Application Support/$appName")
        }
        else -> {
            // Linux/Unix: /home/name/.appName (Standard XDG hidden folder)
            File(userHome, ".$appName")
        }
    }

    if (!dataDir.exists()) {
        dataDir.mkdirs()
    }
    return dataDir
}

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.toClipEntry(): ClipEntry {
    return ClipEntry(StringSelection(this))
}

actual fun openUrl(url: String) {
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(URI(url))
    }
}

actual fun String.urlEncode(): String {
    return URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
        .replace("+", "%20")
}
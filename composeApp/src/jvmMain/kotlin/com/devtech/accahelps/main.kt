package com.devtech.accahelps

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    val dataStore = remember { initDesktopDataStore() }
    val container = remember { AppContainer(dataStore) }
    val state = rememberWindowState()
    val icon = painterResource(Res.drawable.app_logo) // References resources/icon.png
    Window(
        onCloseRequest = ::exitApplication,
        state = state,
        title = "ACCAHelps",
        icon = icon
    ) {
        QuestionPickerApp(repository = container.repository)
    }
}
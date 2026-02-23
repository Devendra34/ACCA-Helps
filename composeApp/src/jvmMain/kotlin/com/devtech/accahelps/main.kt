package com.devtech.accahelps

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    val dataStore = remember { initDesktopDataStore() }
    val container = remember { AppContainer(dataStore) }
    val state = rememberWindowState()

    Window(
        onCloseRequest = ::exitApplication,
        state = state,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.app_logo)
    ) {
        QuestionPickerApp(repository = container.repository)
    }
}
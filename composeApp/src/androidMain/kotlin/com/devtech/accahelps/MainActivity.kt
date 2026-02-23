package com.devtech.accahelps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val dataStore = initAndroidDataStore(applicationContext)
        val container = AppContainer(dataStore)

        setContent {
            QuestionPickerApp(repository = container.repository)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    QuestionPickerApp()
}
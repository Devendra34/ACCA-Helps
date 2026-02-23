package com.devtech.accahelps

import android.app.Application

class MainApp : Application() {

    val appContainer by lazy { AppContainer(filesDir) }
}
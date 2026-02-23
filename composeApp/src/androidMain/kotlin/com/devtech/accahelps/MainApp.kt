package com.devtech.accahelps

import android.app.Application

class MainApp : Application() {

    val appContainer by lazy { AppContainer(filesDir) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: MainApp? = null
            private set
    }
}
package com.devtech.accahelps

import android.app.Application
import com.devtech.accahelps.data.repo.DbQuestionsRepository
import com.devtech.accahelps.data.source.AppDbHelper
import com.devtech.accahelps.db.createDatabase
import com.devtech.accahelps.db.provideDriverFactory
import com.devtech.accahelps.di.AppContainer

class MainApp : Application() {

    val appDbHelper by lazy {
        AppDbHelper(createDatabase(provideDriverFactory()))
    }

    val appContainer by lazy {
        AppContainer(
            appDbHelper = appDbHelper,
            repository = DbQuestionsRepository(appDbHelper)
        )
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: MainApp? = null
            private set
    }
}
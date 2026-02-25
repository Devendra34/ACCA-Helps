package com.devtech.accahelps.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.devtech.accahelps.AppDatabase
import com.devtech.accahelps.MainApp

class AndroidDriverFactory : DriverFactory {
    override fun createDriver(): SqlDriver {
        val context = MainApp.instance!!
        return AndroidSqliteDriver(AppDatabase.Schema, context, DB_NAME)
    }
}

actual fun provideDriverFactory(): DriverFactory = AndroidDriverFactory()
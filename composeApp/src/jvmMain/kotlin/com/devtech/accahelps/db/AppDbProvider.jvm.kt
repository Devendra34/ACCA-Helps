package com.devtech.accahelps.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.devtech.accahelps.AppDatabase
import com.devtech.accahelps.getStorePath
import java.io.File


class JvmDriverFactory : DriverFactory {
    override fun createDriver(): SqlDriver {
        val path = File(getStorePath("ACCA_Buddy"), DB_NAME).absolutePath
        val driver = JdbcSqliteDriver("jdbc:sqlite:$path")
        AppDatabase.Schema.create(driver)
        return driver
    }
}

actual fun provideDriverFactory(): DriverFactory = JvmDriverFactory()
package com.devtech.accahelps.db

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.devtech.accahelps.AppDatabase
import com.devtech.accahelps.QuestionEntity
import com.devtech.accahelps.SectionSelectionEntity
import com.devtech.accahelps.model.Section
import com.devtech.accahelps.model.Source
import com.devtech.accahelps.model.toSectionOrNull
import com.devtech.accahelps.model.toSourceOrNull

interface DriverFactory {
    fun createDriver(): SqlDriver
}

expect fun provideDriverFactory(): DriverFactory

fun createDatabase(driverFactory: DriverFactory): AppDatabase {
    val driver = driverFactory.createDriver()
    val sourceAdapter = SourceAdapter()
    val sectionAdapter = SectionAdapter()
    return AppDatabase(
        driver, QuestionEntity.Adapter(
            sectionAdapter = sectionAdapter,
            sourceAdapter = sourceAdapter,
        ),
        SectionSelectionEntity.Adapter(
            sectionAdapter = sectionAdapter,
            selectedSourcesAdapter = SourcesAdapter(),
        )
    )
}

const val DB_NAME = "acca_buddy.db"

class SectionAdapter : ColumnAdapter<Section, String> {
    override fun decode(databaseValue: String): Section {
        return databaseValue.toSectionOrNull()!!
    }

    override fun encode(value: Section): String {
        return value.id
    }
}

class SourceAdapter : ColumnAdapter<Source, String> {
    override fun decode(databaseValue: String): Source {
        return databaseValue.toSourceOrNull()!!
    }

    override fun encode(value: Source): String {
        return value.label
    }
}

class SourcesAdapter : ColumnAdapter<HashSet<Source>, String> {
    override fun decode(databaseValue: String): HashSet<Source> =
        if (databaseValue.isEmpty()) hashSetOf()
        else databaseValue.split(",").map { Source.valueOf(it) }.toHashSet()

    override fun encode(value: HashSet<Source>): String =
        value.joinToString(separator = ",") { it.name }
}

package com.devtech.accahelps.sheets.impl

import com.devtech.accahelps.sheets.SpreadsheetParser

class RegexCsvParser : SpreadsheetParser {
    override fun parse(rawCsv: String): List<List<String>> {
        val rows = rawCsv.split(Regex("\\r?\\n")).filter { it.isNotBlank() }

        // Regex to handle commas inside quotes
        val csvRegex = Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")

        return rows.map { row ->
            val cols = row.split(csvRegex).map { cell ->
                cell.trim()
                    .removeSurrounding("\"")
                    .replace("\"\"", "\"") // Unescape quotes
            }
            cols
        }
    }
}
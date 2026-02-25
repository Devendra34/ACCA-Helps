package com.devtech.accahelps.sheets


interface SpreadsheetParser {
    fun parse(rawCsv: String): List<List<String>>
}
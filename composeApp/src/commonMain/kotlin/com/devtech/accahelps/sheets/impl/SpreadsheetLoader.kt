package com.devtech.accahelps.sheets.impl

import com.devtech.accahelps.sheets.SpreadsheetParser
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

class SpreadsheetLoader(
    private val parser: SpreadsheetParser = RegexCsvParser()
) {
    private val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.NONE
        }
    }


    suspend fun fetchTable(): List<List<String>> {
        return try {
            val url = "https://docs.google.com/spreadsheets/d/$sheetId/export?format=csv"
            val response = client.get(url).bodyAsText()

            parser.parse(response)
        } catch (e: Exception) {
            println("Network or Parsing Error: ${e.message}")
            emptyList()
        }
    }
}

private const val sheetId = "1pIwMEt1CvdqKfw9K04Hccw2H7DlfnH_pCX7_4G1M6QQ"
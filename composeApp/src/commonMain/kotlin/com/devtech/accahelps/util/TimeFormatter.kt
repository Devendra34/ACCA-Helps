package com.devtech.accahelps.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

object TimeFormatter {

    fun formatCurrentTime(): String {
        return format(Clock.System.now())
    }

    fun format(instant: Instant): String {
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val day = localDateTime.day
        val month = localDateTime.month.name.lowercase()
            .replaceFirstChar { it.uppercase() }
            .take(3)

        val year = localDateTime.year

        val hour12 = when (val h = localDateTime.hour) {
            0 -> 12
            in 1..12 -> h
            else -> h - 12
        }

        val amPm = if (localDateTime.hour < 12) "AM" else "PM"

        val minute = localDateTime.minute.toString().padStart(2, '0')

        val daySuffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }

        return "$day$daySuffix $month $year : $hour12:$minute$amPm"
    }
}
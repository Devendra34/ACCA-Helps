package com.devtech.accahelps.model

enum class Source(val label: String) {
    Kaplan("Kaplan"),
    Bpp("BPP"),
    StudyHub("Study Hub")
}


fun String.toSourceOrNull(): Source? = Source.entries.find {
    it.label.equals(this, ignoreCase = true)
}.also {
    if (it == null) {
        println("Error parsing Source: $this")
    }
}
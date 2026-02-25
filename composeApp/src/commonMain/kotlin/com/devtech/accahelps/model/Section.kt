package com.devtech.accahelps.model

enum class Section(val id: String, val label: String) {
    A("A", "Section A"),
    B("B", "Section B"),
    C("C", "Section C"),
}


fun String.toSectionOrNull(): Section? = Section.entries.find {
    it.id.equals(this, ignoreCase = true)
}.also {
    if (it == null) {
        println("Error parsing Source: $this")
    }
}

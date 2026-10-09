package com.example.vkcourse

object InputValidation {
    fun hasText(value: String): Boolean = value.isNotBlank()

    // Formatting is allowed; dialling codes, extensions and arbitrary URI input are rejected.
    fun normalizePhone(value: String): String? {
        val compact = value.filterNot(Char::isWhitespace)
        if (!compact.matches(Regex("\\+?(?:[0-9-]|\\([0-9][0-9-]*\\))+"))) return null
        val normalized = compact.filterNot { it == '(' || it == ')' || it == '-' }
        return normalized.takeIf { it.matches(Regex("\\+?[0-9]{3,15}")) }
    }
}

package com.example.vkcourse

object InputValidation {
    fun hasText(value: String): Boolean = value.isNotBlank()

    // Номер можно вводить с пробелами, скобками и дефисами — перед набором убираем их.
    fun normalizePhone(value: String): String? {
        val compact = value.filterNot(Char::isWhitespace)
        // Плюс допускаем только в начале, а скобки должны быть парными.
        if (!compact.matches(Regex("\\+?(?:[0-9-]|\\([0-9][0-9-]*\\))+"))) return null
        val normalized = compact.filterNot { it == '(' || it == ')' || it == '-' }
        // Проверяем длину номера. Буквы, добавочные номера и команды вроде *123# не пропускаем.
        return normalized.takeIf { it.matches(Regex("\\+?[0-9]{3,15}")) }
    }
}

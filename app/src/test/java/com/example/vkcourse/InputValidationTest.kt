package com.example.vkcourse

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InputValidationTest {
    @Test
    fun textRejectsBlankButAllowsUnicodeAndSurroundingSpaces() {
        listOf("", " ", "\t\n", "\u00a0").forEach { assertFalse(InputValidation.hasText(it)) }
        assertTrue(InputValidation.hasText(" Привет, Android! 👋 "))
    }

    @Test
    fun phoneAcceptsCommonFormattingAndShortNumbers() {
        mapOf(
            "+7 (999) 123-45-67" to "+79991234567",
            "8 999 123 45 67" to "89991234567",
            "(999)123-45-67" to "9991234567",
            " +1\u00a0(202) 555-0123 " to "+12025550123",
            "112" to "112",
            "+123456789012345" to "+123456789012345",
        ).forEach { (input, expected) -> assertEquals(input, expected, InputValidation.normalizePhone(input)) }
    }

    @Test
    fun phoneRejectsMalformedNumbersAndDiallingCommands() {
        listOf(
            "", "  ", "+", "12", "1234567890123456", "hello", "123abc",
            "++79991234567", "799+91234567", "tel:1234567", "123#", "*123#",
            "123;456", "123,456", "(9991234567", "999)1234567", "((999))1234567",
            "()1234567", "(---)1234567",
        ).forEach { assertNull(it, InputValidation.normalizePhone(it)) }
    }
}

package com.example.vkcourse

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Проверяем пакет приложения на устройстве или эмуляторе.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Берём контекст самого приложения, а не тестового пакета.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.vkcourse", appContext.packageName)
    }
}

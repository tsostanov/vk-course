package com.example.vkcourse.applist.data.mapper

import com.example.vkcourse.applist.data.dto.CatalogAppDto
import com.example.vkcourse.applist.domain.model.CatalogApp
import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogAppMapperTest {
    @Test
    fun mapsCatalogFieldsWithoutInventingDeveloperOrLocalIcon() {
        val dto = CatalogAppDto("id", "QuickNote", "Заметки", "Работа", "https://example.com/icon.png")

        assertEquals(
            CatalogApp("id", "QuickNote", "Заметки", "Работа", "https://example.com/icon.png"),
            CatalogAppMapper.toDomain(dto),
        )
    }

    @Test
    fun mapsEveryDomainFieldToDto() {
        val app = CatalogApp("app-42", "Почта", "Письма и вложения", "Общение", "https://example.com/mail.png")

        assertEquals(
            CatalogAppDto("app-42", "Почта", "Письма и вложения", "Общение", "https://example.com/mail.png"),
            CatalogAppMapper.toDto(app),
        )
    }

    @Test
    fun preservesEmptyFieldsInBothDirections() {
        val dto = CatalogAppDto("", "", "", "", "")
        val app = CatalogApp("", "", "", "", "")

        assertEquals(app, CatalogAppMapper.toDomain(dto))
        assertEquals(dto, CatalogAppMapper.toDto(app))
    }

    @Test
    fun preservesWhitespaceUnicodeAndUrlQueryInBothDirections() {
        val dto = CatalogAppDto("id/42", "  Заметки 📝  ", "Первая строка\nВторая строка", "Работа & учёба", "https://example.com/icon?q=1&lang=ru")
        val app = CatalogApp("id/42", "  Заметки 📝  ", "Первая строка\nВторая строка", "Работа & учёба", "https://example.com/icon?q=1&lang=ru")

        assertEquals(app, CatalogAppMapper.toDomain(dto))
        assertEquals(dto, CatalogAppMapper.toDto(app))
    }

    @Test
    fun dtoRoundTripDoesNotLoseData() {
        val dto = CatalogAppDto("dto-id", "Календарь", "Встречи", "Планы", "https://example.com/calendar.png")

        assertEquals(dto, CatalogAppMapper.toDto(CatalogAppMapper.toDomain(dto)))
    }

    @Test
    fun domainRoundTripDoesNotLoseData() {
        val app = CatalogApp("domain-id", "Музыка", "Плейлисты", "Аудио", "https://example.com/music.png")

        assertEquals(app, CatalogAppMapper.toDomain(CatalogAppMapper.toDto(app)))
    }
}

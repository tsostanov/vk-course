package com.example.vkcourse.applist.data.mapper

import com.example.vkcourse.applist.data.dto.AppDetailsDto
import com.example.vkcourse.applist.data.dto.CatalogAppDto
import com.example.vkcourse.applist.domain.model.AppDetails
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
    fun mapsAllDetailedAppFieldsIncludingScreenshots() {
        val dto = AppDetailsDto(
            "id", "QuickNote", "QuickSoft", "Работа", 3, 8.2,
            "https://example.com/icon.png", listOf("https://example.com/screen.png"), "Описание",
        )

        assertEquals(
            AppDetails(
                "id", "QuickNote", "QuickSoft", "Работа", 3, 8.2,
                "https://example.com/icon.png", listOf("https://example.com/screen.png"), "Описание",
            ),
            AppDetailsMapper.toDomain(dto),
        )
    }
}

package com.example.vkcourse.applist.data.mapper

import com.example.vkcourse.applist.data.dto.AppDetailsDto
import com.example.vkcourse.applist.domain.model.AppDetails
import org.junit.Assert.assertEquals
import org.junit.Test

class AppDetailsMapperTest {
    private val dto = AppDetailsDto(
        "details-id", "QuickNote", "QuickSoft", "Работа", 12, 8.25,
        "https://example.com/icon.png", listOf("https://example.com/first.png", "https://example.com/second.png"),
        "Полное описание",
    )
    private val app = AppDetails(
        "details-id", "QuickNote", "QuickSoft", "Работа", 12, 8.25,
        "https://example.com/icon.png", listOf("https://example.com/first.png", "https://example.com/second.png"),
        "Полное описание",
    )

    @Test
    fun mapsEveryDtoFieldIncludingRenamedScreenshotList() {
        assertEquals(app, AppDetailsMapper.toDomain(dto))
    }

    @Test
    fun mapsEveryDomainFieldIncludingRenamedScreenshotList() {
        assertEquals(dto, AppDetailsMapper.toDto(app))
    }

    @Test
    fun preservesEmptyScreenshotsInBothDirections() {
        assertEquals(app.copy(screenshotUrls = emptyList()), AppDetailsMapper.toDomain(dto.copy(screenshotUrlList = emptyList())))
        assertEquals(dto.copy(screenshotUrlList = emptyList()), AppDetailsMapper.toDto(app.copy(screenshotUrls = emptyList())))
    }

    @Test
    fun preservesScreenshotOrderAndDuplicatesInBothDirections() {
        val screenshots = listOf("third.png", "first.png", "third.png")

        assertEquals(screenshots, AppDetailsMapper.toDomain(dto.copy(screenshotUrlList = screenshots)).screenshotUrls)
        assertEquals(screenshots, AppDetailsMapper.toDto(app.copy(screenshotUrls = screenshots)).screenshotUrlList)
    }

    @Test
    fun preservesEmptyTextZeroAgeAndZeroSizeInBothDirections() {
        val emptyDto = AppDetailsDto("", "", "", "", 0, 0.0, "", emptyList(), "")
        val emptyApp = AppDetails("", "", "", "", 0, 0.0, "", emptyList(), "")

        assertEquals(emptyApp, AppDetailsMapper.toDomain(emptyDto))
        assertEquals(emptyDto, AppDetailsMapper.toDto(emptyApp))
    }

    @Test
    fun dtoRoundTripDoesNotLoseData() {
        val original = dto.copy(description = "  Описание 📝\nВторая строка  ", size = 123.456789)

        assertEquals(original, AppDetailsMapper.toDto(AppDetailsMapper.toDomain(original)))
    }

    @Test
    fun domainRoundTripDoesNotLoseData() {
        val original = app.copy(developer = "  Разработчик & Co  ", ageRating = 18, size = 987.654321)

        assertEquals(original, AppDetailsMapper.toDomain(AppDetailsMapper.toDto(original)))
    }
}

package com.example.vkcourse.applist.data.mapper

import com.example.vkcourse.applist.data.dto.CatalogAppDto
import com.example.vkcourse.applist.domain.model.AppIcon
import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogAppMapperTest {
    @Test
    fun mapsDtoFieldsToDomainModel() {
        val dto = CatalogAppDto(
            id = "music",
            name = "VK Музыка",
            description = "Любимые треки.",
            category = "Музыка",
            developer = "VK",
            iconKey = "music",
        )

        val app = CatalogAppMapper.toDomain(dto)

        assertEquals("music", app.id)
        assertEquals("VK Музыка", app.name)
        assertEquals("Любимые треки.", app.description)
        assertEquals("Музыка", app.category)
        assertEquals("VK", app.developer)
        assertEquals(AppIcon.MUSIC, app.icon)
    }

    @Test(expected = IllegalStateException::class)
    fun rejectsUnknownIconKey() {
        CatalogAppMapper.toDomain(
            CatalogAppDto(
                id = "unknown",
                name = "Unknown",
                description = "Unknown app.",
                category = "Other",
                developer = "Other",
                iconKey = "missing",
            ),
        )
    }
}

package com.example.vkcourse.applist.data.repository

import com.example.vkcourse.applist.data.dto.CatalogAppDto
import com.example.vkcourse.applist.data.source.AppCatalogDataSource
import com.example.vkcourse.applist.data.source.HardcodedAppCatalogDataSource
import com.example.vkcourse.applist.domain.model.AppIcon
import org.junit.Assert.assertEquals
import org.junit.Test

class AppRepositoryImplTest {
    @Test
    fun returnsNineMappedDomainModelsFromHardcodedSource() {
        val apps = AppRepositoryImpl(HardcodedAppCatalogDataSource()).getApps()

        assertEquals(9, apps.size)
        assertEquals("ВКонтакте", apps.first().name)
        assertEquals(AppIcon.SOCIAL, apps.first().icon)
        assertEquals("VK Образование", apps.first { it.id == "education" }.name)
        assertEquals(AppIcon.VIDEO, apps.first { it.id == "clips" }.icon)
    }

    @Test
    fun repositoryReadsSourceAndMapsReturnedDtos() {
        var sourceCallCount = 0
        val dataSource = object : AppCatalogDataSource {
            override fun getApps(): List<CatalogAppDto> {
                sourceCallCount++
                return listOf(
                    CatalogAppDto(
                        id = "test",
                        name = "Тест",
                        description = "Описание",
                        category = "Категория",
                        developer = "Разработчик",
                        iconKey = "books",
                    ),
                )
            }
        }

        val app = AppRepositoryImpl(dataSource).getApps().single()

        assertEquals(1, sourceCallCount)
        assertEquals("test", app.id)
        assertEquals("Тест", app.name)
        assertEquals("Описание", app.description)
        assertEquals("Категория", app.category)
        assertEquals("Разработчик", app.developer)
        assertEquals(AppIcon.BOOKS, app.icon)
    }
}

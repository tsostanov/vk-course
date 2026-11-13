package com.example.vkcourse.applist

import com.example.vkcourse.applist.data.dto.CatalogAppDto
import com.example.vkcourse.applist.data.network.CatalogApi
import java.io.IOException
import kotlinx.coroutines.delay

// Данные только для UI-тестов: приложение использует настоящий Retrofit API.
class FakeCatalogApi : CatalogApi {
    var failNextRequest = false
    var returnEmptyCatalog = false

    override suspend fun getRating(id: String): Float {
        delay(1_500)
        return 4.2f
    }

    override suspend fun getCatalog(): List<CatalogAppDto> {
        if (failNextRequest) {
            failNextRequest = false
            throw IOException("Test network error")
        }
        if (returnEmptyCatalog) return emptyList()
        return listOf(
            CatalogAppDto("vk", "ВКонтакте", "Друзья, сообщества и истории о том, что вам близко.", "Социальные сети", ""),
            CatalogAppDto("music", "VK Музыка", "Музыка и плейлисты.", "Музыка", ""),
            CatalogAppDto("video", "VK Видео", "Видео и трансляции.", "Видео", ""),
            CatalogAppDto("mail", "Почта Mail", "Все письма в одном месте.", "Работа", ""),
            CatalogAppDto("cloud", "Облако Mail", "Файлы и фотографии.", "Инструменты", ""),
            CatalogAppDto("maps", "Карты", "Места и маршруты.", "Навигация", ""),
            CatalogAppDto("books", "Книги", "Книги на каждый день.", "Книги", ""),
            CatalogAppDto("education", "VK Образование", "Курсы и практика для нового шага в IT.", "Образование", ""),
            CatalogAppDto("clips", "VK Клипы", "Короткие видео.", "Видео", ""),
            CatalogAppDto("notes", "Заметки", "Идеи и списки дел.", "Работа", ""),
            CatalogAppDto("camera", "Камера", "Фото и видео.", "Фото", ""),
            CatalogAppDto("calendar", "Календарь", "Встречи и события.", "Работа", ""),
        )
    }

    override suspend fun getAppDetails(id: String) = error("Details endpoint must not be used by catalog UI")
    override suspend fun getCatalogAppDetails(id: String) = error("Details endpoint must not be used by catalog UI")
}

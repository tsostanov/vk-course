package com.example.vkcourse.applist.data.source

import com.example.vkcourse.applist.data.dto.CatalogAppDto

class HardcodedAppCatalogDataSource : AppCatalogDataSource {
    override fun getApps() = listOf(
        CatalogAppDto(
            id = "vk",
            name = "ВКонтакте",
            description = "Друзья, сообщества и истории о том, что вам близко.",
            category = "Социальные сети",
            developer = "VK",
            iconKey = "social",
        ),
        CatalogAppDto(
            id = "music",
            name = "VK Музыка",
            description = "Любимые треки и новые плейлисты под настроение.",
            category = "Музыка",
            developer = "VK",
            iconKey = "music",
        ),
        CatalogAppDto(
            id = "video",
            name = "VK Видео",
            description = "Видео, шоу и трансляции любимых авторов.",
            category = "Видео",
            developer = "VK",
            iconKey = "video",
        ),
        CatalogAppDto(
            id = "mail",
            name = "Почта Mail",
            description = "Все письма в одном месте. Личные и рабочие.",
            category = "Производительность",
            developer = "Mail",
            iconKey = "mail",
        ),
        CatalogAppDto(
            id = "cloud",
            name = "Облако Mail",
            description = "Фотографии и документы, которые всегда с вами.",
            category = "Инструменты",
            developer = "Mail",
            iconKey = "cloud",
        ),
        CatalogAppDto(
            id = "maps",
            name = "Карты",
            description = "Места, маршруты и идеи для следующей прогулки.",
            category = "Путешествия",
            developer = "VK",
            iconKey = "maps",
        ),
        CatalogAppDto(
            id = "books",
            name = "Книги",
            description = "Истории, к которым хочется возвращаться.",
            category = "Книги и справочники",
            developer = "VK",
            iconKey = "books",
        ),
        CatalogAppDto(
            id = "education",
            name = "VK Образование",
            description = "Курсы и практика для нового шага в IT.",
            category = "Образование",
            developer = "VK",
            iconKey = "education",
        ),
        CatalogAppDto(
            id = "clips",
            name = "VK Клипы",
            description = "Короткие видео и рекомендации авторов для любого настроения.",
            category = "Видео",
            developer = "VK",
            iconKey = "video",
        ),
    )
}

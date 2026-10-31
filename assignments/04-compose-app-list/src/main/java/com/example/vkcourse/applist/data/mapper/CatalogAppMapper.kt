package com.example.vkcourse.applist.data.mapper

import com.example.vkcourse.applist.data.dto.CatalogAppDto
import com.example.vkcourse.applist.domain.model.AppIcon
import com.example.vkcourse.applist.domain.model.CatalogApp

object CatalogAppMapper {
    fun toDomain(dto: CatalogAppDto) = CatalogApp(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        category = dto.category,
        developer = dto.developer,
        icon = dto.iconKey.toDomainIcon(),
    )

    private fun String.toDomainIcon(): AppIcon = when (this) {
        "social" -> AppIcon.SOCIAL
        "music" -> AppIcon.MUSIC
        "video" -> AppIcon.VIDEO
        "mail" -> AppIcon.MAIL
        "cloud" -> AppIcon.CLOUD
        "maps" -> AppIcon.MAPS
        "books" -> AppIcon.BOOKS
        "education" -> AppIcon.EDUCATION
        else -> error("Unknown catalog icon key: $this")
    }
}

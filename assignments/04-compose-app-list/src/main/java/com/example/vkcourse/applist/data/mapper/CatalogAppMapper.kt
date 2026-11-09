package com.example.vkcourse.applist.data.mapper

import com.example.vkcourse.applist.data.dto.CatalogAppDto
import com.example.vkcourse.applist.domain.model.CatalogApp

object CatalogAppMapper {
    fun toDomain(dto: CatalogAppDto) = CatalogApp(
        id = dto.id,
        name = dto.name,
        description = dto.description,
        category = dto.category,
        iconUrl = dto.iconUrl,
    )
}

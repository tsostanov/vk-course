package com.example.vkcourse.applist.data.mapper

import com.example.vkcourse.applist.data.dto.AppDetailsDto
import com.example.vkcourse.applist.domain.model.AppDetails

object AppDetailsMapper {
    fun toDto(app: AppDetails) = AppDetailsDto(
        id = app.id,
        name = app.name,
        developer = app.developer,
        category = app.category,
        ageRating = app.ageRating,
        size = app.size,
        iconUrl = app.iconUrl,
        screenshotUrlList = app.screenshotUrls,
        description = app.description,
    )

    fun toDomain(dto: AppDetailsDto) = AppDetails(
        id = dto.id,
        name = dto.name,
        developer = dto.developer,
        category = dto.category,
        ageRating = dto.ageRating,
        size = dto.size,
        iconUrl = dto.iconUrl,
        screenshotUrls = dto.screenshotUrlList,
        description = dto.description,
    )
}

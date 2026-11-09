package com.example.vkcourse.applist.data.mapper

import com.example.vkcourse.applist.data.dto.AppDetailsDto
import com.example.vkcourse.applist.domain.model.AppDetails

object AppDetailsMapper {
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

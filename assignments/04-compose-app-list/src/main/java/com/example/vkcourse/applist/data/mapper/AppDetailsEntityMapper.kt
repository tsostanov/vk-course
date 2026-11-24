package com.example.vkcourse.applist.data.mapper

import com.example.vkcourse.applist.data.local.AppDetailsEntity
import com.example.vkcourse.applist.domain.model.AppDetails

object AppDetailsEntityMapper {
    fun toDomain(entity: AppDetailsEntity) = AppDetails(
        id = entity.id,
        name = entity.name,
        developer = entity.developer,
        category = entity.category,
        ageRating = entity.ageRating,
        size = entity.size,
        iconUrl = entity.iconUrl,
        screenshotUrls = entity.screenshotUrls,
        description = entity.description,
        isInWishlist = entity.isInWishlist,
    )

    fun toEntity(app: AppDetails) = AppDetailsEntity(
        id = app.id,
        name = app.name,
        developer = app.developer,
        category = app.category,
        ageRating = app.ageRating,
        size = app.size,
        iconUrl = app.iconUrl,
        screenshotUrls = app.screenshotUrls,
        description = app.description,
        isInWishlist = app.isInWishlist,
    )
}

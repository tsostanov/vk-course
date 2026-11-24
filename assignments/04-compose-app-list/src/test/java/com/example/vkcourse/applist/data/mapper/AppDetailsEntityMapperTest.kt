package com.example.vkcourse.applist.data.mapper

import com.example.vkcourse.applist.domain.model.AppDetails
import org.junit.Assert.assertEquals
import org.junit.Test

class AppDetailsEntityMapperTest {
    @Test
    fun roundTripPreservesWishlistAndAllDetailsForBothStates() {
        for (inWishlist in listOf(false, true)) {
            val app = AppDetails(
                "id", "Заметки", "Developer", "Работа", 12, 10.5, "icon.png",
                listOf("first.png", "second.png"), "Описание", isInWishlist = inWishlist,
            )
            val entity = AppDetailsEntityMapper.toEntity(app)
            assertEquals(inWishlist, entity.isInWishlist)
            assertEquals(app, AppDetailsEntityMapper.toDomain(entity))
        }
    }
}

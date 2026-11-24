package com.example.vkcourse.applist.testutil

import com.example.vkcourse.applist.data.local.AppDetailsDao
import com.example.vkcourse.applist.data.local.AppDetailsEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeAppDetailsDao : AppDetailsDao {
    private val entities = MutableStateFlow<Map<String, AppDetailsEntity>>(emptyMap())

    override fun getAppDetails(id: String) = entities.map { it[id] }

    override suspend fun upsertAppDetails(entity: AppDetailsEntity) {
        entities.value = entities.value + (entity.id to entity)
    }

    override suspend fun updateWishlistStatus(id: String, isInWishlist: Boolean) {
        entities.value[id]?.let { upsertAppDetails(it.copy(isInWishlist = isInWishlist)) }
    }
}

package com.example.vkcourse.applist.domain.repository

import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.model.AppDetails
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    suspend fun getApps(): List<CatalogApp>
    suspend fun loadRatingsSequentially(ids: List<String>): Map<String, Float>
    suspend fun loadRatingsInParallel(ids: List<String>): Map<String, Float>
    suspend fun getAppDetails(id: String): AppDetails
    fun observeAppDetails(id: String): Flow<AppDetails>
    suspend fun toggleWishlist(id: String)
}

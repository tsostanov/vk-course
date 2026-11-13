package com.example.vkcourse.applist.domain.repository

import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.model.AppDetails

interface AppRepository {
    suspend fun getApps(): List<CatalogApp>
    suspend fun loadRatingsSequentially(ids: List<String>): Map<String, Float>
    suspend fun loadRatingsInParallel(ids: List<String>): Map<String, Float>
    suspend fun getAppDetails(id: String): AppDetails
}

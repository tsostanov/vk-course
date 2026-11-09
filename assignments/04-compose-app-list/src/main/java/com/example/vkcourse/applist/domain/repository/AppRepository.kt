package com.example.vkcourse.applist.domain.repository

import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.model.AppDetails

interface AppRepository {
    suspend fun getApps(): List<CatalogApp>
    suspend fun getAppDetails(id: String): AppDetails
}

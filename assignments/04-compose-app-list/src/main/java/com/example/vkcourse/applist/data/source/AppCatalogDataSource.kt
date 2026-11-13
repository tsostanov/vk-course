package com.example.vkcourse.applist.data.source

import com.example.vkcourse.applist.data.dto.CatalogAppDto
import com.example.vkcourse.applist.data.dto.AppDetailsDto

interface AppCatalogDataSource {
    suspend fun getApps(): List<CatalogAppDto>
    suspend fun getRating(id: String): Float
    suspend fun getAppDetails(id: String): AppDetailsDto
}

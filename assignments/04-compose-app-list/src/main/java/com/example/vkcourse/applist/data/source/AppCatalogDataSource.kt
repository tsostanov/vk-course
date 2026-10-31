package com.example.vkcourse.applist.data.source

import com.example.vkcourse.applist.data.dto.CatalogAppDto

interface AppCatalogDataSource {
    fun getApps(): List<CatalogAppDto>
}

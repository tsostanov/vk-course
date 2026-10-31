package com.example.vkcourse.applist.domain.repository

import com.example.vkcourse.applist.domain.model.CatalogApp

interface AppRepository {
    fun getApps(): List<CatalogApp>
}

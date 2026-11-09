package com.example.vkcourse.applist.data.repository

import com.example.vkcourse.applist.data.mapper.CatalogAppMapper
import com.example.vkcourse.applist.data.source.AppCatalogDataSource
import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.repository.AppRepository
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val dataSource: AppCatalogDataSource,
) : AppRepository {
    override fun getApps(): List<CatalogApp> =
        dataSource.getApps().map(CatalogAppMapper::toDomain)
}

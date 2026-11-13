package com.example.vkcourse.applist.data.repository

import com.example.vkcourse.applist.data.mapper.CatalogAppMapper
import com.example.vkcourse.applist.data.mapper.AppDetailsMapper
import com.example.vkcourse.applist.data.source.AppCatalogDataSource
import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.repository.AppRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class AppRepositoryImpl @Inject constructor(
    private val dataSource: AppCatalogDataSource,
) : AppRepository {
    override suspend fun getApps(): List<CatalogApp> =
        dataSource.getApps().map(CatalogAppMapper::toDomain)

    override suspend fun getAppDetails(id: String) =
        AppDetailsMapper.toDomain(dataSource.getAppDetails(id))

    override suspend fun loadRatingsSequentially(ids: List<String>): Map<String, Float> =
        ids.associateWith { id -> dataSource.getRating(id) }

    override suspend fun loadRatingsInParallel(ids: List<String>): Map<String, Float> = coroutineScope {
        ids.map { id -> async { id to dataSource.getRating(id) } }.awaitAll().toMap()
    }
}

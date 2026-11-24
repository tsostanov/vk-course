package com.example.vkcourse.applist.data.repository

import com.example.vkcourse.applist.data.mapper.CatalogAppMapper
import com.example.vkcourse.applist.data.mapper.AppDetailsMapper
import com.example.vkcourse.applist.data.local.AppDetailsDao
import com.example.vkcourse.applist.data.mapper.AppDetailsEntityMapper
import com.example.vkcourse.applist.domain.model.AppDetails
import com.example.vkcourse.applist.data.source.AppCatalogDataSource
import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.repository.AppRepository
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

class AppRepositoryImpl @Inject constructor(
    private val dataSource: AppCatalogDataSource,
    private val appDetailsDao: AppDetailsDao,
) : AppRepository {
    override suspend fun getApps(): List<CatalogApp> =
        dataSource.getApps().map(CatalogAppMapper::toDomain)

    override suspend fun getAppDetails(id: String): AppDetails {
        val remote = AppDetailsMapper.toDomain(dataSource.getAppDetails(id))
        val saved = appDetailsDao.saveRemoteDetails(AppDetailsEntityMapper.toEntity(remote))
        return AppDetailsEntityMapper.toDomain(saved)
    }

    override fun observeAppDetails(id: String): Flow<AppDetails> =
        appDetailsDao.getAppDetails(id)
            .filterNotNull()
            .map(AppDetailsEntityMapper::toDomain)
            .distinctUntilChanged()

    override suspend fun toggleWishlist(id: String) = appDetailsDao.toggleWishlist(id)

    override suspend fun loadRatingsSequentially(ids: List<String>): Map<String, Float> =
        ids.associateWith { id -> dataSource.getRating(id) }

    override suspend fun loadRatingsInParallel(ids: List<String>): Map<String, Float> = coroutineScope {
        ids.map { id -> async { id to dataSource.getRating(id) } }.awaitAll().toMap()
    }
}

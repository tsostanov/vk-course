package com.example.vkcourse.applist.domain.usecase

import com.example.vkcourse.applist.domain.model.AppDetails
import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.repository.AppRepository

internal class StubAppRepository : AppRepository {
    var catalog: suspend () -> List<CatalogApp> = { error("Unexpected catalog request") }
    var sequential: suspend (List<String>) -> Map<String, Float> = { error("Unexpected sequential request") }
    var parallel: suspend (List<String>) -> Map<String, Float> = { error("Unexpected parallel request") }
    var catalogCalls = 0
        private set
    val sequentialCalls = mutableListOf<List<String>>()
    val parallelCalls = mutableListOf<List<String>>()

    override suspend fun getApps(): List<CatalogApp> {
        catalogCalls++
        return catalog()
    }

    override suspend fun loadRatingsSequentially(ids: List<String>): Map<String, Float> {
        sequentialCalls.add(ids.toList())
        return sequential(ids)
    }

    override suspend fun loadRatingsInParallel(ids: List<String>): Map<String, Float> {
        parallelCalls.add(ids.toList())
        return parallel(ids)
    }

    override suspend fun getAppDetails(id: String): AppDetails = error("Unexpected details request: $id")
}

package com.example.vkcourse.applist.data.repository

import com.example.vkcourse.applist.data.network.CatalogApi
import com.example.vkcourse.applist.data.source.RemoteAppCatalogDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.measureTimeMillis

/** Реальные delay и часы: runTest с виртуальным временем здесь не используется. */
class AppRatingsBenchmarkTest {
    @Test
    fun measureSequentialAndParallelForTwelveApps() = runBlocking {
        val api = Retrofit.Builder()
            .baseUrl("http://localhost/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatalogApi::class.java)
        val repository = AppRepositoryImpl(RemoteAppCatalogDataSource(api))
        val ids = (1..12).map { "app-$it" }
        val sequential: Map<String, Float>
        val sequentialMs = measureTimeMillis { sequential = repository.loadRatingsSequentially(ids) }
        val parallel: Map<String, Float>
        val parallelMs = measureTimeMillis { parallel = repository.loadRatingsInParallel(ids) }
        println("RATINGS_BENCHMARK count=${ids.size} sequentialMs=$sequentialMs parallelMs=$parallelMs")
        assertEquals(ids.toSet(), sequential.keys)
        assertEquals(ids.toSet(), parallel.keys)
        assertTrue((sequential.values + parallel.values).all { it in 3f..5f })
    }
}

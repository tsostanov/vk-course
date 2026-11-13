package com.example.vkcourse.applist.data.repository

import com.example.vkcourse.applist.data.dto.AppDetailsDto
import com.example.vkcourse.applist.data.dto.CatalogAppDto
import com.example.vkcourse.applist.data.source.AppCatalogDataSource
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppRatingsTest {
    private val ids = (1..12).map { "app-$it" }

    @Test
    fun sequentialWaitsForEachRatingAndParallelStartsAllTwelve() = runTest {
        var active = 0
        var peak = 0
        val repository = repository { id ->
            active++
            peak = maxOf(peak, active)
            try {
                delay(1_500)
                3f + ids.indexOf(id) / 10f
            } finally {
                active--
            }
        }
        val start = currentTime
        val sequential = repository.loadRatingsSequentially(ids)
        assertEquals(18_000L, currentTime - start)
        assertEquals(1, peak)
        peak = 0
        val parallelStart = currentTime
        val parallel = repository.loadRatingsInParallel(ids)
        assertEquals(1_500L, currentTime - parallelStart)
        assertEquals(12, peak)
        assertEquals(ids.toSet(), parallel.keys)
        assertEquals(sequential, parallel)
    }

    @Test
    fun failingChildCancelsSiblingRequestsAndPropagatesError() = runTest {
        var cancelledSiblings = 0
        val repository = repository { id ->
            if (id == ids.first()) {
                delay(100)
                throw IOException("Rating failed")
            }
            try {
                awaitCancellation()
            } finally {
                cancelledSiblings++
            }
        }
        try {
            repository.loadRatingsInParallel(ids)
            fail("Expected failure")
        } catch (_: IOException) {
            assertEquals(11, cancelledSiblings)
        }
    }

    @Test
    fun cancellingParentCancelsEveryParallelRequest() = runTest {
        var active = 0
        val repository = repository {
            active++
            try {
                awaitCancellation()
            } finally {
                active--
            }
        }
        val parent = launch { repository.loadRatingsInParallel(ids) }
        runCurrent()
        assertEquals(12, active)
        parent.cancel()
        parent.join()
        assertEquals(0, active)
        assertTrue(parent.isCancelled)
    }

    @Test
    fun emptyIdsDoNotRequestRatings() = runTest {
        val repository = repository { error("No requests expected") }
        assertEquals(emptyMap<String, Float>(), repository.loadRatingsSequentially(emptyList()))
        assertEquals(emptyMap<String, Float>(), repository.loadRatingsInParallel(emptyList()))
    }

    private fun repository(rating: suspend (String) -> Float) = AppRepositoryImpl(
        object : AppCatalogDataSource {
            override suspend fun getApps(): List<CatalogAppDto> = error("Not needed")
            override suspend fun getAppDetails(id: String): AppDetailsDto = error("Not needed")
            override suspend fun getRating(id: String) = rating(id)
        },
    )
}

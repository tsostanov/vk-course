package com.example.vkcourse.applist.data.repository

import com.example.vkcourse.applist.data.network.CatalogApi
import com.example.vkcourse.applist.data.source.RemoteAppCatalogDataSource
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppRepositoryImplTest {
    private lateinit var server: MockWebServer
    private lateinit var repository: AppRepositoryImpl

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CatalogApi::class.java)
        repository = AppRepositoryImpl(RemoteAppCatalogDataSource(api))
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun simulatedRatingRunsOnRetrofitApiWithoutHttpRequest() = runTest {
        val ratings = repository.loadRatingsInParallel(listOf("one", "two"))
        assertEquals(setOf("one", "two"), ratings.keys)
        org.junit.Assert.assertTrue(ratings.values.all { it in 3f..5f })
        assertEquals(0, server.requestCount)
    }

    @Test
    fun loadsCatalogWithGetAndMapsServerFields() = runTest {
        server.enqueue(jsonResponse("catalog.json"))

        val app = repository.getApps().single()

        assertEquals("GET", server.takeRequest().method)
        assertEquals("fa2e31b8-1234-4cf7-9914-108a170a1b01", app.id)
        assertEquals("QuickNote", app.name)
        assertEquals("Лёгкое приложение для заметок и напоминаний", app.description)
        assertEquals("Производительность", app.category)
        assertEquals("https://example.com/icon.png", app.iconUrl)
    }

    @Test
    fun loadsFullDetailsUsingIdFromCatalog() = runTest {
        server.enqueue(jsonResponse("catalog.json"))
        server.enqueue(jsonResponse("details.json"))
        val id = repository.getApps().single().id
        assertEquals("/catalog", server.takeRequest().path)

        val details = repository.getAppDetails(id)

        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/$id", request.path)
        assertEquals(id, details.id)
        assertEquals("QuickSoft Inc.", details.developer)
        assertEquals(3, details.ageRating)
        assertEquals(8.2, details.size, 0.001)
        assertEquals(listOf("https://example.com/screenshot.png"), details.screenshotUrls)
        assertEquals("Подробное описание QuickNote", details.description)
    }

    @Test
    fun triesCatalogDetailsPathWhenAssignmentPathReturns404() = runTest {
        server.enqueue(MockResponse().setResponseCode(404))
        server.enqueue(jsonResponse("details.json"))
        val id = "fa2e31b8-1234-4cf7-9914-108a170a1b01"

        assertEquals(id, repository.getAppDetails(id).id)
        assertEquals("/$id", server.takeRequest().path)
        assertEquals("/catalog/$id", server.takeRequest().path)
    }

    @Test
    fun doesNotHideServerErrorsBehindDetailsFallback() = runTest {
        server.enqueue(MockResponse().setResponseCode(500))
        try {
            repository.getAppDetails("test-id")
            fail("Expected HTTP error")
        } catch (error: HttpException) {
            assertEquals(500, error.code())
        }
        assertEquals(1, server.requestCount)
    }

    @Test
    fun propagatesCatalogHttpErrors() = runTest {
        server.enqueue(MockResponse().setResponseCode(503))
        try {
            repository.getApps()
            fail("Expected HTTP error")
        } catch (error: HttpException) {
            assertEquals(503, error.code())
        }
        assertEquals("/catalog", server.takeRequest().path)
    }

    private fun jsonResponse(resource: String) = MockResponse()
        .setHeader("Content-Type", "application/json; charset=utf-8")
        .setBody(checkNotNull(javaClass.classLoader?.getResource(resource)).readText())
}

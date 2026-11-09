package com.example.vkcourse.applist

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.vkcourse.applist.data.repository.AppRepositoryImpl
import com.example.vkcourse.applist.data.source.RemoteAppCatalogDataSource
import com.example.vkcourse.applist.di.NetworkModule
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

// Запускается отдельно с аргументом liveApi=true, поскольку требует доступного учебного сервера.
@RunWith(AndroidJUnit4::class)
class LiveCatalogApiTest {
    @Test
    fun loadsCatalogAndDetailsFromTrainingServer() = runBlocking {
        assumeTrue(InstrumentationRegistry.getArguments().getString("liveApi") == "true")
        val retrofit = NetworkModule.provideRetrofit(NetworkModule.provideOkHttpClient())
        val api = NetworkModule.provideCatalogApi(retrofit)
        val repository = AppRepositoryImpl(RemoteAppCatalogDataSource(api))

        val catalog = repository.getApps()
        assertTrue(catalog.isNotEmpty())
        val first = catalog.first()
        assertTrue(first.name.isNotBlank())
        assertTrue(first.iconUrl.startsWith("https://"))

        val details = repository.getAppDetails(first.id)
        assertEquals(first.id, details.id)
        assertEquals(first.name, details.name)
        assertTrue(details.developer.isNotBlank())
        assertTrue(details.description.isNotBlank())
        assertTrue(details.screenshotUrls.isNotEmpty())
    }
}

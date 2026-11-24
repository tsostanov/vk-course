package com.example.vkcourse.applist

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vkcourse.applist.data.dto.AppDetailsDto
import com.example.vkcourse.applist.data.dto.CatalogAppDto
import com.example.vkcourse.applist.data.local.AppDatabase
import com.example.vkcourse.applist.data.mapper.AppDetailsEntityMapper
import com.example.vkcourse.applist.data.repository.AppRepositoryImpl
import com.example.vkcourse.applist.data.source.AppCatalogDataSource
import java.util.UUID
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WishlistPersistenceTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val databaseName = "wishlist-test-${UUID.randomUUID()}.db"
    private lateinit var database: AppDatabase
    private lateinit var repository: AppRepositoryImpl
    private var description = "Initial description"
    private val source = object : AppCatalogDataSource {
        override suspend fun getApps(): List<CatalogAppDto> = error("Not used")
        override suspend fun getRating(id: String): Float = error("Not used")
        override suspend fun getAppDetails(id: String) = AppDetailsDto(
            id, "App $id", "Developer", "Tools", 3, 8.2, "icon.png",
            listOf("https://example.com/one?q=comma,quote\"", "second.png"), description,
        )
    }

    @Before
    fun setUp() = openDatabase()

    private fun openDatabase() {
        database = Room.databaseBuilder(context, AppDatabase::class.java, databaseName).build()
        repository = AppRepositoryImpl(source, database.appDetailsDao())
    }

    @After
    fun tearDown() {
        database.close()
        context.deleteDatabase(databaseName)
    }

    @Test
    fun databaseEmitsBothWishlistChangesWithoutAnotherNetworkRequest() = runBlocking {
        repository.getAppDetails("one")
        assertFalse(repository.observeAppDetails("one").first().isInWishlist)
        val added = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(5_000) { repository.observeAppDetails("one").first { it.isInWishlist } }
        }
        repository.toggleWishlist("one")
        assertTrue(added.await().isInWishlist)
        val removed = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(5_000) { repository.observeAppDetails("one").first { !it.isInWishlist } }
        }
        repository.toggleWishlist("one")
        assertFalse(removed.await().isInWishlist)
    }

    @Test
    fun wishlistSurvivesDatabaseReopeningAndServerRefreshAndIsScopedToId() = runBlocking {
        val initial = repository.getAppDetails("one")
        repository.getAppDetails("two")
        repository.toggleWishlist("one")
        database.close()
        openDatabase()

        val restored = repository.observeAppDetails("one").first()
        assertEquals(initial.copy(isInWishlist = true), restored)
        assertFalse(repository.observeAppDetails("two").first().isInWishlist)
        description = "Updated from server"
        assertTrue(repository.getAppDetails("one").isInWishlist)
        val updated = repository.observeAppDetails("one").first()
        assertEquals(description, updated.description)
        assertTrue(updated.isInWishlist)
        val entity = AppDetailsEntityMapper.toEntity(updated)
        assertEquals(entity, database.appDetailsDao().getAppDetails("one").first())
    }

    @Test
    fun concurrentTogglesAndRefreshDoNotLoseUserChanges() = runBlocking {
        repository.getAppDetails("one")
        (1..21).map {
            async(kotlinx.coroutines.Dispatchers.Default) { repository.toggleWishlist("one") }
        }.plus(async(kotlinx.coroutines.Dispatchers.Default) { repository.getAppDetails("one") }).awaitAll()
        assertTrue(repository.observeAppDetails("one").first().isInWishlist)
        repository.toggleWishlist("one")
        assertFalse(repository.observeAppDetails("one").first().isInWishlist)
    }

    @Test
    fun missingIdDoesNotChangeOtherApps() = runBlocking {
        repository.getAppDetails("one")
        repository.toggleWishlist("missing")
        assertFalse(repository.observeAppDetails("one").first().isInWishlist)
        assertEquals(null, database.appDetailsDao().getAppDetails("missing").first())
    }
}

package com.example.vkcourse.applist.data.repository

import com.example.vkcourse.applist.data.dto.AppDetailsDto
import com.example.vkcourse.applist.data.dto.CatalogAppDto
import com.example.vkcourse.applist.data.source.AppCatalogDataSource
import com.example.vkcourse.applist.domain.model.AppDetails
import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.testutil.assertSuspendThrows
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/** Изолированные тесты репозитория: источник данных подменён, Retrofit и HTTP не используются. */
class AppRepositoryUnitTest {
    private val dto = CatalogAppDto("app-id", "Заметки", "Короткое описание", "Работа", "https://example.com/icon.png")
    private val app = CatalogApp("app-id", "Заметки", "Короткое описание", "Работа", "https://example.com/icon.png")

    @Test
    fun mapsCatalogFieldsAndPreservesSourceOrder() = runTest {
        val repository = repository(catalog = { listOf(dto.copy(id = "second"), dto) })

        assertEquals(listOf(app.copy(id = "second"), app), repository.getApps())
    }

    @Test
    fun emptySourceProducesEmptyCatalog() = runTest {
        val repository = repository(catalog = { emptyList() })

        assertEquals(emptyList<CatalogApp>(), repository.getApps())
    }

    @Test
    fun everyCatalogCallReadsFreshSourceDataExactlyOnce() = runTest {
        var calls = 0
        var current = listOf(dto)
        val repository = repository(catalog = { calls++; current })
        assertEquals(listOf(app), repository.getApps())
        assertEquals(1, calls)
        current = listOf(dto.copy(name = "Обновлённые заметки"))

        assertEquals(listOf(app.copy(name = "Обновлённые заметки")), repository.getApps())
        assertEquals(2, calls)
    }

    @Test
    fun forwardsDetailsIdExactlyOnceAndMapsEveryField() = runTest {
        val requestedIds = mutableListOf<String>()
        val repository = repository(details = { id ->
            requestedIds.add(id)
            AppDetailsDto(
                id, "Почта", "MailSoft", "Общение", 18, 23.75,
                "https://example.com/mail.png", listOf("screen-2.png", "screen-1.png"), "Полное описание почты",
            )
        })

        assertEquals(
            AppDetails(
                "details-42", "Почта", "MailSoft", "Общение", 18, 23.75,
                "https://example.com/mail.png", listOf("screen-2.png", "screen-1.png"), "Полное описание почты",
            ),
            repository.getAppDetails("details-42"),
        )
        assertEquals(listOf("details-42"), requestedIds)
    }

    @Test
    fun propagatesCatalogSourceFailure() = runTest {
        val failure = IOException("Catalog unavailable")
        val repository = repository(catalog = { throw failure })

        assertSame(failure, assertSuspendThrows<IOException> { repository.getApps() })
    }

    @Test
    fun propagatesDetailsSourceFailure() = runTest {
        val failure = IOException("Details unavailable")
        val repository = repository(details = { throw failure })

        assertSame(failure, assertSuspendThrows<IOException> { repository.getAppDetails("missing") })
    }

    @Test
    fun propagatesCatalogCancellation() = runTest {
        val cancellation = CancellationException("Catalog cancelled")
        val repository = repository(catalog = { throw cancellation })

        assertSame(cancellation, assertSuspendThrows<CancellationException> { repository.getApps() })
    }

    @Test
    fun propagatesDetailsCancellation() = runTest {
        val cancellation = CancellationException("Details cancelled")
        val repository = repository(details = { throw cancellation })

        assertSame(cancellation, assertSuspendThrows<CancellationException> { repository.getAppDetails("app-id") })
    }

    private fun repository(
        catalog: suspend () -> List<CatalogAppDto> = { error("Unexpected catalog request") },
        details: suspend (String) -> AppDetailsDto = { error("Unexpected details request") },
    ) = AppRepositoryImpl(object : AppCatalogDataSource {
        override suspend fun getApps() = catalog()
        override suspend fun getAppDetails(id: String) = details(id)
        override suspend fun getRating(id: String): Float = error("Unexpected rating request: $id")
    })
}

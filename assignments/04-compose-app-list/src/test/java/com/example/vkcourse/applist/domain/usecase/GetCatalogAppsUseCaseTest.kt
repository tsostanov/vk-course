package com.example.vkcourse.applist.domain.usecase

import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.testutil.assertSuspendThrows
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GetCatalogAppsUseCaseTest {
    private val repository = StubAppRepository()
    private val useCase = GetCatalogAppsUseCase(repository)
    private val app = CatalogApp("first", "Заметки", "Описание", "Работа", "https://example.com/icon.png")

    @Test
    fun returnsCatalogWithoutChangingFieldsOrOrder() = runTest {
        val apps = listOf(app.copy(id = "second"), app)
        repository.catalog = { apps }

        assertEquals(apps, useCase())
    }

    @Test
    fun returnsEmptyCatalog() = runTest {
        repository.catalog = { emptyList() }

        assertEquals(emptyList<CatalogApp>(), useCase())
    }

    @Test
    fun requestsCatalogExactlyOnceWithoutRequestingRatings() = runTest {
        repository.catalog = { listOf(app) }

        useCase()

        assertEquals(1, repository.catalogCalls)
        assertEquals(emptyList<List<String>>(), repository.sequentialCalls)
        assertEquals(emptyList<List<String>>(), repository.parallelCalls)
    }

    @Test
    fun propagatesRepositoryFailureWithoutRetrying() = runTest {
        val failure = IOException("Offline")
        repository.catalog = { throw failure }

        assertSame(failure, assertSuspendThrows<IOException> { useCase() })
        assertEquals(1, repository.catalogCalls)
    }

    @Test
    fun propagatesCancellation() = runTest {
        val cancellation = CancellationException("Catalog cancelled")
        repository.catalog = { throw cancellation }

        assertSame(cancellation, assertSuspendThrows<CancellationException> { useCase() })
    }

    @Test
    fun subsequentInvocationReturnsFreshRepositoryData() = runTest {
        repository.catalog = { listOf(app) }
        assertEquals(listOf(app), useCase())
        val updated = app.copy(name = "Новое название")
        repository.catalog = { listOf(updated) }

        assertEquals(listOf(updated), useCase())
        assertEquals(2, repository.catalogCalls)
    }
}

package com.example.vkcourse.applist.domain.usecase

import com.example.vkcourse.applist.testutil.assertSuspendThrows
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class LoadAppRatingsUseCaseTest {
    private val repository = StubAppRepository()
    private val useCase = LoadAppRatingsUseCase(repository)
    private val ids = listOf("second", "first")

    @Test
    fun sequentialModeReturnsRatingsAndCallsOnlySequentialMethod() = runTest {
        val ratings = mapOf("second" to 3.1f, "first" to 4.9f)
        repository.sequential = { ratings }

        assertEquals(ratings, useCase(ids, RatingLoadMode.Sequential))
        assertEquals(listOf(ids), repository.sequentialCalls)
        assertEquals(emptyList<List<String>>(), repository.parallelCalls)
        assertEquals(0, repository.catalogCalls)
    }

    @Test
    fun parallelModeReturnsRatingsAndCallsOnlyParallelMethod() = runTest {
        val ratings = mapOf("second" to 4.2f, "first" to 5f)
        repository.parallel = { ratings }

        assertEquals(ratings, useCase(ids, RatingLoadMode.Parallel))
        assertEquals(listOf(ids), repository.parallelCalls)
        assertEquals(emptyList<List<String>>(), repository.sequentialCalls)
        assertEquals(0, repository.catalogCalls)
    }

    @Test
    fun emptyIdsAreDelegatedToSelectedMethodInBothModes() = runTest {
        repository.sequential = { emptyMap() }
        repository.parallel = { emptyMap() }

        for (mode in RatingLoadMode.entries) {
            assertEquals(emptyMap<String, Float>(), useCase(emptyList(), mode))
        }
        assertEquals(listOf(emptyList<String>()), repository.sequentialCalls)
        assertEquals(listOf(emptyList<String>()), repository.parallelCalls)
    }

    @Test
    fun preservesInputOrderAndDuplicatesInBothModes() = runTest {
        val duplicatedIds = listOf("third", "first", "third")
        repository.sequential = { emptyMap() }
        repository.parallel = { emptyMap() }

        for (mode in RatingLoadMode.entries) useCase(duplicatedIds, mode)

        assertEquals(listOf(duplicatedIds), repository.sequentialCalls)
        assertEquals(listOf(duplicatedIds), repository.parallelCalls)
    }

    @Test
    fun propagatesSequentialFailureWithoutFallingBackToParallel() = runTest {
        val failure = IOException("Sequential failure")
        repository.sequential = { throw failure }

        assertSame(failure, assertSuspendThrows<IOException> { useCase(ids, RatingLoadMode.Sequential) })
        assertEquals(1, repository.sequentialCalls.size)
        assertEquals(emptyList<List<String>>(), repository.parallelCalls)
    }

    @Test
    fun propagatesParallelFailureWithoutFallingBackToSequential() = runTest {
        val failure = IOException("Parallel failure")
        repository.parallel = { throw failure }

        assertSame(failure, assertSuspendThrows<IOException> { useCase(ids, RatingLoadMode.Parallel) })
        assertEquals(1, repository.parallelCalls.size)
        assertEquals(emptyList<List<String>>(), repository.sequentialCalls)
    }

    @Test
    fun propagatesCancellationInBothModes() = runTest {
        val cancellation = CancellationException("Ratings cancelled")
        repository.sequential = { throw cancellation }
        repository.parallel = { throw cancellation }

        for (mode in RatingLoadMode.entries) {
            assertSame(cancellation, assertSuspendThrows<CancellationException> { useCase(ids, mode) })
        }
    }

    @Test
    fun repeatedInvocationUsesNewIdsAndNewlySelectedMode() = runTest {
        repository.sequential = { mapOf("old" to 3f) }
        repository.parallel = { mapOf("new" to 5f) }

        assertEquals(mapOf("old" to 3f), useCase(listOf("old"), RatingLoadMode.Sequential))
        assertEquals(mapOf("new" to 5f), useCase(listOf("new"), RatingLoadMode.Parallel))
        assertEquals(listOf(listOf("old")), repository.sequentialCalls)
        assertEquals(listOf(listOf("new")), repository.parallelCalls)
    }
}

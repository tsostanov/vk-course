package com.example.vkcourse.applist.presentation

import com.example.vkcourse.applist.domain.model.AppDetails
import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.repository.AppRepository
import com.example.vkcourse.applist.domain.usecase.GetCatalogAppsUseCase
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppCatalogViewModelTest {
    private val app = CatalogApp("id", "QuickNote", "Заметки", "Работа", "https://example.com/icon.png")

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun showsLoadingThenServerCatalog() = runTest {
        val viewModel = viewModel { listOf(app) }
        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle()

        assertEquals(AppCatalogUiState(apps = listOf(app), isLoading = false), viewModel.uiState.value)
    }

    @Test
    fun networkFailureCanBeRetriedSuccessfully() = runTest {
        var calls = 0
        val viewModel = viewModel {
            if (++calls == 1) throw IOException("Offline")
            listOf(app)
        }
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.hasError)
        assertFalse(viewModel.uiState.value.isLoading)

        viewModel.loadCatalog()
        advanceUntilIdle()

        assertEquals(2, calls)
        assertFalse(viewModel.uiState.value.hasError)
        assertEquals(listOf(app), viewModel.uiState.value.apps)
    }

    @Test
    fun emptyResponseFinishesLoadingWithoutError() = runTest {
        val viewModel = viewModel { emptyList() }
        advanceUntilIdle()

        assertEquals(AppCatalogUiState(isLoading = false), viewModel.uiState.value)
    }

    @Test
    fun doesNotStartDuplicateRequestsWhileLoading() = runTest {
        var calls = 0
        val viewModel = viewModel {
            calls++
            delay(1_000)
            listOf(app)
        }
        runCurrent()
        repeat(3) { viewModel.loadCatalog() }
        advanceUntilIdle()

        assertEquals(1, calls)
        assertEquals(listOf(app), viewModel.uiState.value.apps)
    }

    @Test
    fun cancellationIsNotShownAsNetworkFailure() = runTest {
        val viewModel = viewModel { throw CancellationException() }
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.hasError)
    }

    private fun viewModel(load: suspend () -> List<CatalogApp>): AppCatalogViewModel {
        val repository = object : AppRepository {
            override suspend fun getApps() = load()
            override suspend fun getAppDetails(id: String): AppDetails = error("Not used by catalog UI")
        }
        return AppCatalogViewModel(GetCatalogAppsUseCase(repository))
    }
}

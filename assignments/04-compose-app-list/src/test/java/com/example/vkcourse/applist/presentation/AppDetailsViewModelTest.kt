package com.example.vkcourse.applist.presentation

import androidx.lifecycle.SavedStateHandle
import com.example.vkcourse.applist.domain.model.AppDetails
import com.example.vkcourse.applist.domain.usecase.StubAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class AppDetailsViewModelTest {
    @Before
    fun setUp() = Dispatchers.setMain(StandardTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun progressesEvery150msAndFinishesAfterThreeSeconds() = runTest {
        val viewModel = createViewModel()
        runCurrent()
        assertEquals(InstallState.NotInstalled, viewModel.installState())
        viewModel.onInstallClick()
        assertEquals(InstallState.Installing(0), viewModel.installState())
        advanceTimeBy(150)
        runCurrent()
        assertEquals(InstallState.Installing(5), viewModel.installState())
        advanceTimeBy(2_850)
        runCurrent()
        assertEquals(InstallState.Installed, viewModel.installState())
        viewModel.onInstallClick()
        assertEquals(InstallState.Installed, viewModel.installState())
    }

    @Test
    fun cancellationStopsUpdatesAndAllowsFreshInstall() = runTest {
        val viewModel = createViewModel()
        runCurrent()
        viewModel.onInstallClick()
        advanceTimeBy(750)
        runCurrent()
        assertEquals(InstallState.Installing(25), viewModel.installState())
        viewModel.onInstallClick()
        advanceUntilIdle()
        assertEquals(InstallState.NotInstalled, viewModel.installState())
        viewModel.onInstallClick()
        assertEquals(InstallState.Installing(0), viewModel.installState())
        advanceUntilIdle()
        assertEquals(InstallState.Installed, viewModel.installState())
    }

    @Test
    fun rapidStartCancelStartDoesNotLetOldJobOverwriteNewInstall() = runTest {
        val viewModel = createViewModel()
        runCurrent()
        repeat(3) { viewModel.onInstallClick() }
        advanceTimeBy(150)
        runCurrent()
        assertEquals(InstallState.Installing(5), viewModel.installState())
        advanceUntilIdle()
        assertEquals(InstallState.Installed, viewModel.installState())
    }

    @Test
    fun clearingViewModelCancelsInstallation() = runTest {
        val store = ViewModelStore()
        val viewModel = ViewModelProvider.create(
            store,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
                    createViewModel() as T
            },
        )[AppDetailsViewModel::class]
        runCurrent()
        viewModel.onInstallClick()
        runCurrent()
        store.clear()
        advanceUntilIdle()
        assertEquals(InstallState.Installing(0), viewModel.installState())
    }

    @Test
    fun wishlistChangesOnlyAfterDatabaseEmissionAndPreservesInstallation() = runTest {
        var requestedId: String? = null
        repository.toggle = { requestedId = it }
        val viewModel = createViewModel()
        runCurrent()
        viewModel.onInstallClick()
        viewModel.toggleWishlist()
        runCurrent()
        assertEquals("vk", requestedId)
        assertFalse((viewModel.state.value as AppDetailsState.Content).isInWishlist)

        observed.value = app.copy(isInWishlist = true)
        runCurrent()
        assertTrue((viewModel.state.value as AppDetailsState.Content).isInWishlist)
        assertEquals(InstallState.Installing(0), viewModel.installState())
        observed.value = app
        runCurrent()
        assertFalse((viewModel.state.value as AppDetailsState.Content).isInWishlist)
        advanceUntilIdle()
    }

    @Test
    fun oneShotResponseDoesNotSetContentWithoutDatabaseEmission() = runTest {
        repository.observeDetails = { emptyFlow() }
        val viewModel = createViewModel()
        runCurrent()
        assertEquals(AppDetailsState.Loading, viewModel.state.value)
    }

    @Test
    fun loadingFailureShowsErrorAndCanBeRetriedThroughDatabase() = runTest {
        repository.observeDetails = { emptyFlow() }
        repository.details = { throw IOException("Offline") }
        val viewModel = createViewModel()
        runCurrent()
        assertEquals(AppDetailsState.Error, viewModel.state.value)

        repository.observeDetails = { observed }
        repository.details = { app }
        viewModel.loadAppDetails()
        runCurrent()
        assertEquals(app, (viewModel.state.value as AppDetailsState.Content).appDetails)
    }

    @Test
    fun failedRefreshKeepsCachedWishlistAvailable() = runTest {
        observed.value = app.copy(isInWishlist = true)
        repository.details = { throw IOException("Offline") }
        val viewModel = createViewModel()
        runCurrent()
        assertTrue((viewModel.state.value as AppDetailsState.Content).isInWishlist)
    }

    @Test
    fun failedToggleKeepsContentAndDoesNotChangeWishlist() = runTest {
        repository.toggle = { throw IOException("Disk full") }
        val viewModel = createViewModel()
        runCurrent()
        viewModel.toggleWishlist()
        runCurrent()
        val content = viewModel.state.value as AppDetailsState.Content
        assertTrue(content.wishlistError)
        assertFalse(content.isInWishlist)
    }

    @Test
    fun observationFailureShowsError() = runTest {
        repository.observeDetails = { flow { throw IOException("Database unavailable") } }
        val viewModel = createViewModel()
        runCurrent()
        assertEquals(AppDetailsState.Error, viewModel.state.value)
    }

    private val app = AppDetails("vk", "VK", "VK", "Social", 12, 10.0, "", emptyList(), "Description")
    private val observed = MutableStateFlow(app)
    private val repository = StubAppRepository().apply {
        details = { app }
        observeDetails = { observed }
    }

    private fun createViewModel() = AppDetailsViewModel(repository, SavedStateHandle(mapOf("appId" to "vk")))

    private fun AppDetailsViewModel.installState() = (state.value as AppDetailsState.Content).installState
}

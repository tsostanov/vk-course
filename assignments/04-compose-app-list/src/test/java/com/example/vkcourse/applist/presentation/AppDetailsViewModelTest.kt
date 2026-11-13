package com.example.vkcourse.applist.presentation

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
        val viewModel = AppDetailsViewModel()
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
        val viewModel = AppDetailsViewModel()
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
        val viewModel = AppDetailsViewModel()
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
                    AppDetailsViewModel() as T
            },
        )[AppDetailsViewModel::class]
        viewModel.onInstallClick()
        runCurrent()
        store.clear()
        advanceUntilIdle()
        assertEquals(InstallState.Installing(0), viewModel.installState())
    }

    private fun AppDetailsViewModel.installState() = (state.value as AppDetailsState.Content).installState
}

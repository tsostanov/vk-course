package com.example.vkcourse.applist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface InstallState {
    data object NotInstalled : InstallState
    data class Installing(val progress: Int) : InstallState
    data object Installed : InstallState
}

sealed interface AppDetailsState {
    data class Content(val installState: InstallState = InstallState.NotInstalled) : AppDetailsState
}

@HiltViewModel
class AppDetailsViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<AppDetailsState>(AppDetailsState.Content())
    val state = _state.asStateFlow()
    private var installJob: Job? = null

    fun onInstallClick() {
        val current = _state.value as? AppDetailsState.Content ?: return
        when (current.installState) {
            is InstallState.Installing -> {
                installJob?.cancel()
                installJob = null
                updateInstallState(InstallState.NotInstalled)
            }
            InstallState.NotInstalled -> {
                // Сразу меняем состояние, чтобы повторный клик отменял запуск.
                updateInstallState(InstallState.Installing(0))
                installJob = viewModelScope.launch { emulateInstall() }
            }
            InstallState.Installed -> Unit
        }
    }

    private suspend fun emulateInstall() {
        for (progress in 5..100 step 5) {
            delay(150)
            updateInstallState(InstallState.Installing(progress))
        }
        updateInstallState(InstallState.Installed)
    }

    private fun updateInstallState(installState: InstallState) {
        _state.update { current ->
            if (current is AppDetailsState.Content) current.copy(installState = installState) else current
        }
    }
}

package com.example.vkcourse.applist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.vkcourse.applist.domain.model.AppDetails
import com.example.vkcourse.applist.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface InstallState {
    data object NotInstalled : InstallState
    data class Installing(val progress: Int) : InstallState
    data object Installed : InstallState
}

sealed interface AppDetailsState {
    data object Loading : AppDetailsState
    data object Error : AppDetailsState
    data class Content(
        val appDetails: AppDetails,
        val installState: InstallState = InstallState.NotInstalled,
        val wishlistError: Boolean = false,
    ) : AppDetailsState {
        val isInWishlist: Boolean get() = appDetails.isInWishlist
    }
}

@HiltViewModel
class AppDetailsViewModel @Inject constructor(
    private val appRepository: AppRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val appId: String = checkNotNull(savedStateHandle["appId"])
    private val _state = MutableStateFlow<AppDetailsState>(AppDetailsState.Loading)
    val state = _state.asStateFlow()
    private var installJob: Job? = null
    private var loadJob: Job? = null
    private var observeJob: Job? = null

    init {
        observeAppDetails()
        loadAppDetails()
    }

    private fun observeAppDetails() {
        observeJob = viewModelScope.launch {
            appRepository.observeAppDetails(appId)
                .catch { _state.value = AppDetailsState.Error }
                .collect { appDetails ->
                    _state.update { current ->
                        if (current is AppDetailsState.Content) {
                            current.copy(appDetails = appDetails, wishlistError = false)
                        } else {
                            AppDetailsState.Content(appDetails)
                        }
                    }
                }
        }
    }

    fun loadAppDetails() {
        if (loadJob?.isActive == true) return
        if (observeJob?.isActive != true) observeAppDetails()
        if (_state.value !is AppDetailsState.Content) _state.value = AppDetailsState.Loading
        loadJob = viewModelScope.launch {
            try {
                // Content приходит только из БД через подписку, в том числе после загрузки с сервера.
                appRepository.getAppDetails(appId)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _state.update { current ->
                    if (current is AppDetailsState.Content) current else AppDetailsState.Error
                }
            }
        }
    }

    fun toggleWishlist() {
        if (_state.value !is AppDetailsState.Content) return
        viewModelScope.launch {
            try {
                appRepository.toggleWishlist(appId)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _state.update { current ->
                    if (current is AppDetailsState.Content) current.copy(wishlistError = true) else current
                }
            }
        }
    }

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

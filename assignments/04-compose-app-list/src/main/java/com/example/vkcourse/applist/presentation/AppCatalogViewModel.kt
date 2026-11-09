package com.example.vkcourse.applist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.usecase.GetCatalogAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class AppCatalogUiState(
    val apps: List<CatalogApp> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)

sealed interface AppCatalogEvent {
    data object ShowCatalogInfo : AppCatalogEvent
}

@HiltViewModel
class AppCatalogViewModel @Inject constructor(
    private val getCatalogApps: GetCatalogAppsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppCatalogUiState())
    val uiState = _uiState.asStateFlow()

    private val eventsChannel = Channel<AppCatalogEvent>(capacity = Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()
    private var loadJob: Job? = null

    init {
        loadCatalog()
    }

    fun loadCatalog() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            _uiState.value = AppCatalogUiState()
            try {
                val apps = getCatalogApps()
                _uiState.value = AppCatalogUiState(apps = apps, isLoading = false)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                logger.log(Level.WARNING, "Catalog request failed", error)
                _uiState.value = AppCatalogUiState(isLoading = false, hasError = true)
            }
        }
    }

    fun onToolbarInfoClick() {
        viewModelScope.launch {
            eventsChannel.send(AppCatalogEvent.ShowCatalogInfo)
        }
    }

    private companion object {
        val logger: Logger = Logger.getLogger(AppCatalogViewModel::class.java.name)
    }
}

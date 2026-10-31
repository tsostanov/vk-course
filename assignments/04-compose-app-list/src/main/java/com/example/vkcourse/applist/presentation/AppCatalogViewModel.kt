package com.example.vkcourse.applist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.usecase.GetCatalogAppsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppCatalogUiState(
    val apps: List<CatalogApp> = emptyList(),
    val isLoading: Boolean = true,
)

sealed interface AppCatalogEvent {
    data object ShowCatalogInfo : AppCatalogEvent
}

class AppCatalogViewModel(
    private val getCatalogApps: GetCatalogAppsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppCatalogUiState())
    val uiState = _uiState.asStateFlow()

    private val eventsChannel = Channel<AppCatalogEvent>(capacity = Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                getCatalogApps()
            }
            _uiState.value = AppCatalogUiState(apps = apps, isLoading = false)
        }
    }

    fun onToolbarInfoClick() {
        viewModelScope.launch {
            eventsChannel.send(AppCatalogEvent.ShowCatalogInfo)
        }
    }

    class Factory(
        private val getCatalogApps: GetCatalogAppsUseCase,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(AppCatalogViewModel::class.java)) {
                "Unsupported ViewModel type: ${modelClass.name}"
            }
            @Suppress("UNCHECKED_CAST")
            return AppCatalogViewModel(getCatalogApps) as T
        }
    }
}

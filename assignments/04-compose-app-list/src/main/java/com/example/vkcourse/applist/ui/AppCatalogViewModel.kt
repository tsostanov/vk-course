package com.example.vkcourse.applist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkcourse.applist.data.CatalogApp
import com.example.vkcourse.applist.data.sampleApps
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class AppCatalogUiState(
    val apps: List<CatalogApp> = sampleApps,
)

sealed interface AppCatalogEvent {
    data object ShowCatalogInfo : AppCatalogEvent
}

class AppCatalogViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppCatalogUiState())
    val uiState = _uiState.asStateFlow()

    private val eventsChannel = Channel<AppCatalogEvent>(capacity = Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    fun onToolbarInfoClick() {
        viewModelScope.launch {
            eventsChannel.send(AppCatalogEvent.ShowCatalogInfo)
        }
    }
}

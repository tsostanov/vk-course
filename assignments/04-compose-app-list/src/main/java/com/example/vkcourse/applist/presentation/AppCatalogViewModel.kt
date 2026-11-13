package com.example.vkcourse.applist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.usecase.GetCatalogAppsUseCase
import com.example.vkcourse.applist.domain.usecase.LoadAppRatingsUseCase
import com.example.vkcourse.applist.domain.usecase.RatingLoadMode
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

data class AppCatalogUiState(
    val apps: List<CatalogApp> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val ratings: Map<String, Float> = emptyMap(),
    val ratingLoadMode: RatingLoadMode? = null,
    val ratingTimings: Map<RatingLoadMode, Long> = emptyMap(),
    val hasRatingsError: Boolean = false,
)

sealed interface AppCatalogEvent {
    data object ShowCatalogInfo : AppCatalogEvent
}

@HiltViewModel
class AppCatalogViewModel @Inject constructor(
    private val getCatalogApps: GetCatalogAppsUseCase,
    private val loadAppRatings: LoadAppRatingsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AppCatalogUiState())
    val uiState = _uiState.asStateFlow()

    private val eventsChannel = Channel<AppCatalogEvent>(capacity = Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()
    private var loadJob: Job? = null
    private var ratingsJob: Job? = null

    init {
        loadCatalog()
    }

    fun loadCatalog() {
        if (loadJob?.isActive == true) return
        ratingsJob?.cancel()
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

    fun loadRatings(mode: RatingLoadMode) {
        val current = _uiState.value
        if (current.isLoading || current.apps.isEmpty() || ratingsJob?.isActive == true) return
        _uiState.update { it.copy(ratingLoadMode = mode, hasRatingsError = false) }
        ratingsJob = viewModelScope.launch {
            try {
                val ratings: Map<String, Float>
                val elapsed = measureTimeMillis {
                    ratings = loadAppRatings(current.apps.map { it.id }, mode)
                }
                _uiState.update {
                    it.copy(
                        ratings = ratings,
                        ratingLoadMode = null,
                        ratingTimings = it.ratingTimings + (mode to elapsed),
                    )
                }
                logger.info("Ratings: $mode, count=${ratings.size}, elapsed=${elapsed}ms")
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                logger.log(Level.WARNING, "Ratings request failed", error)
                _uiState.update { it.copy(ratingLoadMode = null, hasRatingsError = true) }
            }
        }
    }

    private companion object {
        val logger: Logger = Logger.getLogger(AppCatalogViewModel::class.java.name)
    }
}

package com.example.vkcourse.applist.domain.usecase

import com.example.vkcourse.applist.domain.repository.AppRepository

enum class RatingLoadMode { Sequential, Parallel }

class LoadAppRatingsUseCase(private val repository: AppRepository) {
    suspend operator fun invoke(ids: List<String>, mode: RatingLoadMode): Map<String, Float> =
        when (mode) {
            RatingLoadMode.Sequential -> repository.loadRatingsSequentially(ids)
            RatingLoadMode.Parallel -> repository.loadRatingsInParallel(ids)
        }
}

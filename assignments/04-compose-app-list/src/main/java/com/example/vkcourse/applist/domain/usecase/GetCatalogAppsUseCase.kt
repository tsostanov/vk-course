package com.example.vkcourse.applist.domain.usecase

import com.example.vkcourse.applist.domain.model.CatalogApp
import com.example.vkcourse.applist.domain.repository.AppRepository

class GetCatalogAppsUseCase(
    private val appRepository: AppRepository,
) {
    operator fun invoke(): List<CatalogApp> = appRepository.getApps()
}

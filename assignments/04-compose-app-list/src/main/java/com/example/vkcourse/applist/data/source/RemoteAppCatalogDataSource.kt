package com.example.vkcourse.applist.data.source

import com.example.vkcourse.applist.data.dto.AppDetailsDto
import com.example.vkcourse.applist.data.network.CatalogApi
import javax.inject.Inject
import retrofit2.HttpException

class RemoteAppCatalogDataSource @Inject constructor(
    private val api: CatalogApi,
) : AppCatalogDataSource {
    override suspend fun getApps() = api.getCatalog()

    override suspend fun getAppDetails(id: String): AppDetailsDto = try {
        api.getAppDetails(id)
    } catch (error: HttpException) {
        if (error.code() != 404) throw error
        api.getCatalogAppDetails(id)
    }
}

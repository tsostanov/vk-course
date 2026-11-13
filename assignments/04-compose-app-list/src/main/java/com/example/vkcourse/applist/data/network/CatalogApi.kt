package com.example.vkcourse.applist.data.network

import com.example.vkcourse.applist.data.dto.AppDetailsDto
import com.example.vkcourse.applist.data.dto.CatalogAppDto
import retrofit2.http.GET
import retrofit2.http.Path
import kotlinx.coroutines.delay

interface CatalogApi {
    suspend fun getRating(id: String): Float {
        delay(1_500)
        return (30..50).random() / 10f
    }

    @GET("catalog")
    suspend fun getCatalog(): List<CatalogAppDto>

    @GET("{id}")
    suspend fun getAppDetails(@Path("id") id: String): AppDetailsDto

    // На учебном сервере подробности сейчас доступны по /catalog/{id}.
    @GET("catalog/{id}")
    suspend fun getCatalogAppDetails(@Path("id") id: String): AppDetailsDto
}

package com.example.vkcourse.applist.data.dto

import com.google.gson.annotations.SerializedName

data class AppDetailsDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("developer") val developer: String,
    @SerializedName("category") val category: String,
    @SerializedName("ageRating") val ageRating: Int,
    @SerializedName("size") val size: Double,
    @SerializedName("iconUrl") val iconUrl: String,
    @SerializedName("screenshotUrlList") val screenshotUrlList: List<String>,
    @SerializedName("description") val description: String,
)

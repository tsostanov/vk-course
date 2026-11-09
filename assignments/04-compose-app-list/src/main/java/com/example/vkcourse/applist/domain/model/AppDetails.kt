package com.example.vkcourse.applist.domain.model

data class AppDetails(
    val id: String,
    val name: String,
    val developer: String,
    val category: String,
    val ageRating: Int,
    val size: Double,
    val iconUrl: String,
    val screenshotUrls: List<String>,
    val description: String,
)

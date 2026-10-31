package com.example.vkcourse.applist.domain.model

data class CatalogApp(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val developer: String,
    val icon: AppIcon,
)

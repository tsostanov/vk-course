package com.example.vkcourse.applist.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.vkcourse.applist.R

data class CatalogApp(
    val id: String,
    @param:StringRes @get:StringRes val name: Int,
    @param:StringRes @get:StringRes val description: Int,
    @param:StringRes @get:StringRes val category: Int,
    @param:DrawableRes @get:DrawableRes val icon: Int,
    @param:StringRes @get:StringRes val developer: Int = R.string.developer_vk,
)

// Данные и иконки лежат в проекте: список работает без доступа к сети.
val sampleApps = listOf(
    CatalogApp("vk", R.string.vk_name, R.string.vk_description, R.string.category_social, R.drawable.ic_social),
    CatalogApp("music", R.string.music_name, R.string.music_description, R.string.category_music, R.drawable.ic_music),
    CatalogApp("video", R.string.video_name, R.string.video_description, R.string.category_video, R.drawable.ic_video),
    CatalogApp("mail", R.string.mail_name, R.string.mail_description, R.string.category_productivity, R.drawable.ic_mail, R.string.developer_mail),
    CatalogApp("cloud", R.string.cloud_name, R.string.cloud_description, R.string.category_tools, R.drawable.ic_cloud, R.string.developer_mail),
    CatalogApp("maps", R.string.maps_name, R.string.maps_description, R.string.category_travel, R.drawable.ic_maps),
    CatalogApp("books", R.string.books_name, R.string.books_description, R.string.category_books, R.drawable.ic_books),
    CatalogApp("education", R.string.education_name, R.string.education_description, R.string.category_education, R.drawable.ic_education),
    CatalogApp("clips", R.string.clips_name, R.string.clips_description, R.string.category_video, R.drawable.ic_video),
)

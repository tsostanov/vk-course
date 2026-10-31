package com.example.vkcourse.applist.presentation

import androidx.annotation.DrawableRes
import com.example.vkcourse.applist.R
import com.example.vkcourse.applist.domain.model.AppIcon

@DrawableRes
internal fun AppIcon.toDrawableResource(): Int = when (this) {
    AppIcon.SOCIAL -> R.drawable.ic_social
    AppIcon.MUSIC -> R.drawable.ic_music
    AppIcon.VIDEO -> R.drawable.ic_video
    AppIcon.MAIL -> R.drawable.ic_mail
    AppIcon.CLOUD -> R.drawable.ic_cloud
    AppIcon.MAPS -> R.drawable.ic_maps
    AppIcon.BOOKS -> R.drawable.ic_books
    AppIcon.EDUCATION -> R.drawable.ic_education
}

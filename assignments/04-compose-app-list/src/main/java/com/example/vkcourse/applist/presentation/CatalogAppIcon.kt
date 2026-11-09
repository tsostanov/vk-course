package com.example.vkcourse.applist.presentation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.vkcourse.applist.R

@Composable
internal fun CatalogAppIcon(iconUrl: String, modifier: Modifier = Modifier) {
    val placeholder = painterResource(R.drawable.ic_catalog)
    AsyncImage(
        model = iconUrl,
        // Название приложения уже показано рядом с иконкой.
        contentDescription = null,
        placeholder = placeholder,
        error = placeholder,
        fallback = placeholder,
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
    )
}

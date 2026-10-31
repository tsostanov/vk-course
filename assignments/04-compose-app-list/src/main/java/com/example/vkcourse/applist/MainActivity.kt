package com.example.vkcourse.applist

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vkcourse.applist.data.repository.AppRepositoryImpl
import com.example.vkcourse.applist.data.source.HardcodedAppCatalogDataSource
import com.example.vkcourse.applist.presentation.AppCatalogTheme
import com.example.vkcourse.applist.presentation.AppNavigation

class MainActivity : ComponentActivity() {
    private val appRepository by lazy {
        AppRepositoryImpl(HardcodedAppCatalogDataSource())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Над синим тулбаром используем светлые значки строки состояния.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            AppCatalogTheme {
                AppNavigation(appRepository)
            }
        }
    }
}

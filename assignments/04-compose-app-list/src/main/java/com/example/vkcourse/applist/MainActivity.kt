package com.example.vkcourse.applist

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vkcourse.applist.ui.AppCatalogScreen
import com.example.vkcourse.applist.ui.AppCatalogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Над синим тулбаром используем светлые значки строки состояния.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            AppCatalogTheme {
                AppCatalogScreen()
            }
        }
    }
}

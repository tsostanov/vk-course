package com.example.vkcourse.applist.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vkcourse.applist.data.sampleApps

private const val CATALOG_ROUTE = "catalog"
private const val APP_DETAILS_ROUTE = "app/{appId}"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = CATALOG_ROUTE,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(CATALOG_ROUTE) {
            AppCatalogScreen(
                onAppClick = { app ->
                    navController.navigate("app/${app.id}")
                },
            )
        }
        composable(
            route = APP_DETAILS_ROUTE,
            arguments = listOf(navArgument("appId") { type = NavType.StringType }),
        ) { entry ->
            val appId = requireNotNull(entry.arguments?.getString("appId"))
            val app = requireNotNull(sampleApps.firstOrNull { it.id == appId }) {
                "Unknown app id: $appId"
            }
            AppDetailsScreen(
                app = app,
                onBackClick = { navController.popBackStack() },
                onShowcaseAppClick = {
                    navController.popBackStack(CATALOG_ROUTE, inclusive = false)
                },
            )
        }
    }
}

package com.example.vkcourse.applist.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

private const val CATALOG_ROUTE = "catalog"
private const val APP_DETAILS_ROUTE = "app/{appId}"

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val catalogViewModel: AppCatalogViewModel = hiltViewModel()
    val catalogState by catalogViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = CATALOG_ROUTE,
        modifier = Modifier.fillMaxSize(),
    ) {
        composable(CATALOG_ROUTE) {
            AppCatalogScreen(
                viewModel = catalogViewModel,
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
            val app = catalogState.apps.firstOrNull { it.id == appId }
            if (app == null) {
                // После восстановления процесса каталог ещё может загружаться.
                AppCatalogScreen(
                    viewModel = catalogViewModel,
                    onAppClick = { navController.navigate("app/${it.id}") },
                )
            } else {
                AppDetailsScreen(
                    app = app,
                    showcaseApps = catalogState.apps,
                    onBackClick = { navController.popBackStack() },
                    onShowcaseAppClick = {
                        navController.popBackStack(CATALOG_ROUTE, inclusive = false)
                    },
                )
            }
        }
    }
}

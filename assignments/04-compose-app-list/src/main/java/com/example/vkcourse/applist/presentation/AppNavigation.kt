package com.example.vkcourse.applist.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vkcourse.applist.domain.repository.AppRepository
import com.example.vkcourse.applist.domain.usecase.GetCatalogAppsUseCase

private const val CATALOG_ROUTE = "catalog"
private const val APP_DETAILS_ROUTE = "app/{appId}"

@Composable
fun AppNavigation(appRepository: AppRepository) {
    val navController = rememberNavController()
    val factory = remember(appRepository) {
        AppCatalogViewModel.Factory(GetCatalogAppsUseCase(appRepository))
    }
    val catalogViewModel: AppCatalogViewModel = viewModel(factory = factory)
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
            val app = requireNotNull(catalogState.apps.firstOrNull { it.id == appId }) {
                "Unknown app id: $appId"
            }
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

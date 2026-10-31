package com.example.vkcourse.applist.presentation

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vkcourse.applist.R
import com.example.vkcourse.applist.domain.model.CatalogApp

@Composable
fun AppCatalogScreen(
    viewModel: AppCatalogViewModel,
    onAppClick: (CatalogApp) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMessage = stringResource(R.string.catalog_info_message)

    LaunchedEffect(viewModel, snackbarMessage) {
        viewModel.events.collect { event ->
            when (event) {
                AppCatalogEvent.ShowCatalogInfo -> snackbarHostState.showSnackbar(snackbarMessage)
            }
        }
    }

    AppCatalogContent(
        apps = uiState.apps,
        isLoading = uiState.isLoading,
        onAppClick = onAppClick,
        onToolbarInfoClick = viewModel::onToolbarInfoClick,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
private fun AppCatalogContent(
    apps: List<CatalogApp>,
    isLoading: Boolean,
    onAppClick: (CatalogApp) -> Unit,
    onToolbarInfoClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { CatalogToolbar(onInfoClick = onToolbarInfoClick) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center).testTag("app_list_loading"),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxSize()
                        .testTag("app_list"),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(key = "header", contentType = "header") {
                        CatalogHeader(apps.size)
                    }
                    items(apps, key = { it.id }, contentType = { "app" }) { app ->
                        AppListItem(
                            app = app,
                            onClick = { onAppClick(app) },
                            modifier = Modifier.testTag("app_${app.id}"),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogToolbar(onInfoClick: () -> Unit) {
    val infoDescription = stringResource(R.string.catalog_info_description)

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.catalog_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.ic_catalog),
                contentDescription = stringResource(R.string.catalog_icon_description),
                modifier = Modifier.padding(start = 16.dp, end = 12.dp).size(40.dp),
            )
        },
        actions = {
            IconButton(
                onClick = onInfoClick,
                modifier = Modifier.testTag("toolbar_info"),
            ) {
                Text(
                    text = "i",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics {
                        contentDescription = infoDescription
                    },
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = CatalogBlue,
            titleContentColor = Color.White,
        ),
    )
}

@Composable
private fun CatalogHeader(appCount: Int) {
    Column(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.apps_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = stringResource(R.string.catalog_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = pluralStringResource(R.plurals.app_count, appCount, appCount),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
internal fun AppListItem(
    app: CatalogApp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(16.dp).semantics(mergeDescendants = true) {},
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Image(
                painter = painterResource(app.icon.toDrawableResource()),
                // Название уже есть рядом: TalkBack не нужно повторять его для иконки.
                contentDescription = null,
                modifier = Modifier.size(56.dp).testTag("app_icon_${app.id}"),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = app.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 3.dp),
                )
            }
        }
    }
}

@Preview(name = "Светлая тема", showBackground = true)
@Preview(name = "Тёмная тема", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Крупный шрифт", fontScale = 1.5f, widthDp = 320, heightDp = 720, showBackground = true)
@Composable
private fun AppCatalogPreview() {
    AppCatalogTheme {
        AppCatalogContent(
            apps = emptyList(),
            isLoading = false,
            onAppClick = {},
            onToolbarInfoClick = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

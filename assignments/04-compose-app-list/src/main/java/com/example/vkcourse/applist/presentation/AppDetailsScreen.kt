package com.example.vkcourse.applist.presentation

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vkcourse.applist.R
import com.example.vkcourse.applist.domain.model.AppIcon
import com.example.vkcourse.applist.domain.model.CatalogApp

private const val SHOWCASE_PAGE_SIZE = 3
private const val SHOWCASE_PAGE_COUNT = 3

@Composable
fun AppDetailsScreen(
    app: CatalogApp,
    onBackClick: () -> Unit,
    onShowcaseAppClick: (CatalogApp) -> Unit,
    showcaseApps: List<CatalogApp>,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { DetailsToolbar(onBackClick) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState())
                .testTag("app_details"),
        ) {
            AppDetailsHeader(app)
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.app_details_description),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .semantics { heading() },
            )
            Text(
                text = app.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, end = 20.dp),
            )
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            Spacer(Modifier.height(20.dp))
            Showcase(
                apps = showcaseApps,
                onAppClick = onShowcaseAppClick,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AppDetailsHeader(app: CatalogApp) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(app.icon.toDrawableResource()),
            contentDescription = null,
            modifier = Modifier
                .size(112.dp)
                .clip(RoundedCornerShape(24.dp)),
        )
        Spacer(Modifier.width(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = app.category,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = app.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = app.developer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Showcase(
    apps: List<CatalogApp>,
    onAppClick: (CatalogApp) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { SHOWCASE_PAGE_COUNT })

    Column {
        Text(
            text = stringResource(R.string.showcase_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .semantics { heading() },
        )
        Spacer(Modifier.height(12.dp))
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .testTag("showcase_pager"),
            key = { it },
        ) { page ->
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val pageApps = apps.drop(page * SHOWCASE_PAGE_SIZE).take(SHOWCASE_PAGE_SIZE)
                pageApps.forEach { app ->
                    AppListItem(
                        app = app,
                        onClick = { onAppClick(app) },
                        modifier = Modifier.testTag("showcase_app_${app.id}"),
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(SHOWCASE_PAGE_COUNT) { page ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (page == pagerState.currentPage) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (page == pagerState.currentPage) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            },
                        )
                        .testTag("showcase_indicator_$page"),
                )
            }
            Text(
                text = "${pagerState.currentPage + 1} / $SHOWCASE_PAGE_COUNT",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp).testTag("showcase_page_count"),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailsToolbar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_details_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Text(
                    text = "‹",
                    fontSize = 32.sp,
                    color = Color.White,
                    modifier = Modifier.semantics {
                        this.contentDescription = "Назад"
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

@Preview(name = "Карточка приложения", showBackground = true)
@Preview(
    name = "Карточка приложения · тёмная тема",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Composable
private fun AppDetailsPreview() {
    AppCatalogTheme {
        AppDetailsScreen(
            app = CatalogApp(
                id = "vk",
                name = "ВКонтакте",
                description = "Друзья, сообщества и истории о том, что вам близко.",
                category = "Социальные сети",
                developer = "VK",
                icon = AppIcon.SOCIAL,
            ),
            onBackClick = {},
            onShowcaseAppClick = {},
            showcaseApps = emptyList(),
        )
    }
}

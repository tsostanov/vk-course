package com.example.vkcourse.applist

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToKey
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.lifecycle.ViewModelProvider
import com.example.vkcourse.applist.data.network.CatalogApi
import com.example.vkcourse.applist.di.NetworkModule
import com.example.vkcourse.applist.presentation.AppCatalogViewModel
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@UninstallModules(NetworkModule::class)
class AppCatalogTest {
    private val fakeApi = FakeCatalogApi()

    @BindValue
    @JvmField
    val api: CatalogApi = fakeApi

    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createAndroidComposeRule<MainActivity>()

    @Before
    fun waitForCatalog() {
        hilt.inject()
        waitForTag("app_list")
    }

    private fun waitForTag(tag: String, timeoutMillis: Long = 10_000) {
        compose.waitUntil(timeoutMillis = timeoutMillis) {
            compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun installationCanBeCancelledAndRestartedToCompletion() {
        compose.onNodeWithTag("app_vk").performClick()
        compose.onNodeWithTag("install_button").assertTextContains("Установить").performClick()
        compose.onNodeWithTag("install_button").assertTextContains("Отменить").performClick()
        compose.onNodeWithTag("install_button").assertTextContains("Установить").performClick()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText("Установлено").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithTag("install_button").assertIsNotEnabled()
    }

    @Test
    fun sequentialAndParallelRatingsAppearOnCatalogCardsWithTimings() {
        compose.onNodeWithTag("ratings_Sequential").performClick()
        waitForTag("ratings_time_Sequential", timeoutMillis = 30_000)
        compose.onNodeWithTag("ratings_Parallel").performClick()
        waitForTag("ratings_time_Parallel")
        compose.onNodeWithTag("app_list").performScrollToKey("vk")
        compose.onNodeWithTag("app_rating_vk", useUnmergedTree = true)
            .assertTextContains("★", substring = true)
        compose.onNodeWithTag("app_rating_vk", useUnmergedTree = true)
            .assertTextEquals(compose.activity.getString(R.string.app_rating, 4.2f))
    }

    @Test
    fun launchShowsToolbarAndAllRequiredAppFields() {
        compose.onNodeWithText("Каталог").assertIsDisplayed()
        compose.onNodeWithContentDescription("Иконка приложения «Каталог»").assertIsDisplayed()
        compose.onNodeWithTag("app_icon_vk", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("ВКонтакте", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText(
            "Друзья, сообщества и истории о том, что вам близко.",
            useUnmergedTree = true,
        ).assertIsDisplayed()
        compose.onNodeWithText("Социальные сети", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun lastAppIsReachableAndToolbarStaysVisible() {
        compose.onNodeWithTag("app_list").performScrollToKey("education")
        compose.onNodeWithText("VK Образование", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Курсы и практика для нового шага в IT.", useUnmergedTree = true)
            .assertIsDisplayed()
        compose.onNodeWithText("Образование", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithTag("app_icon_education", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Каталог").assertIsDisplayed()
    }

    @Test
    fun scrollPositionSurvivesActivityRecreation() {
        compose.onNodeWithTag("app_list").performScrollToKey("education")
        compose.onNodeWithText("VK Образование", useUnmergedTree = true).assertIsDisplayed()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("VK Образование", useUnmergedTree = true).assertIsDisplayed()
        compose.onNodeWithText("Каталог").assertIsDisplayed()
    }

    @Test
    fun tappingCatalogItemOpensDetailsWithThreePageShowcase() {
        compose.onNodeWithTag("app_vk").performClick()

        compose.onNodeWithTag("app_details").assertIsDisplayed()
        compose.onNodeWithText("Карточка приложения").assertIsDisplayed()
        compose.onNodeWithText("Витрина").assertIsDisplayed()
        compose.onNodeWithTag("showcase_page_count").performScrollTo()
        compose.onNodeWithText("1 / 3").assertIsDisplayed()
        compose.onNodeWithTag("showcase_app_music").assertIsDisplayed()
    }

    @Test
    fun tappingShowcaseItemReturnsToCatalog() {
        compose.onNodeWithTag("app_vk").performClick()
        compose.onNodeWithTag("showcase_page_count").performScrollTo()
        compose.onNodeWithTag("showcase_app_music").performClick()

        compose.onNodeWithTag("app_list").assertIsDisplayed()
        compose.onNodeWithText("Каталог").assertIsDisplayed()
    }

    @Test
    fun showcaseCanBeSwipedThroughAllThreePages() {
        compose.onNodeWithTag("app_vk").performClick()
        compose.onNodeWithTag("showcase_page_count").performScrollTo()
        val pager = compose.onNodeWithTag("showcase_pager")

        pager.performTouchInput { swipeLeft() }
        compose.onNodeWithText("2 / 3").assertIsDisplayed()
        pager.performTouchInput { swipeLeft() }
        compose.onNodeWithText("3 / 3").assertIsDisplayed()
        compose.onNodeWithTag("showcase_app_clips").assertIsDisplayed()
    }

    @Test
    fun toolbarSnackbarIsNotShownAgainAfterActivityRecreation() {
        compose.onNodeWithTag("toolbar_info").performClick()
        compose.onNodeWithText("Каталог приложений загружается с сервера.").assertIsDisplayed()

        compose.activityRule.scenario.recreate()

        compose.onNodeWithTag("app_list").assertIsDisplayed()
        compose.onNodeWithText("ВКонтакте", useUnmergedTree = true).assertIsDisplayed()
        compose.onAllNodesWithText("Каталог приложений загружается с сервера.").assertCountEquals(0)
    }

    @Test
    fun networkErrorShowsRetryAndRetryLoadsCatalog() {
        compose.runOnIdle {
            fakeApi.failNextRequest = true
            ViewModelProvider(compose.activity)[AppCatalogViewModel::class.java].loadCatalog()
        }
        waitForTag("catalog_retry")
        compose.onNodeWithText("Не удалось загрузить каталог. Проверьте подключение и попробуйте ещё раз.")
            .assertIsDisplayed()

        compose.onNodeWithTag("catalog_retry").performClick()

        waitForTag("app_list")
        compose.onNodeWithText("ВКонтакте", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun emptyCatalogShowsMessageAndReloadButton() {
        compose.runOnIdle {
            fakeApi.returnEmptyCatalog = true
            ViewModelProvider(compose.activity)[AppCatalogViewModel::class.java].loadCatalog()
        }
        waitForTag("catalog_retry")
        compose.onNodeWithText("В каталоге пока нет приложений.").assertIsDisplayed()
        compose.onNodeWithTag("catalog_retry").assertIsDisplayed()
    }
}

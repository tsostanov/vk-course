package com.example.vkcourse.applist

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToKey
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppCatalogTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

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
        compose.onNodeWithText("1 / 3").assertIsDisplayed()
        compose.onNodeWithTag("showcase_app_music").assertIsDisplayed()
    }

    @Test
    fun tappingShowcaseItemReturnsToCatalog() {
        compose.onNodeWithTag("app_vk").performClick()
        compose.onNodeWithTag("showcase_app_music").performClick()

        compose.onNodeWithTag("app_list").assertIsDisplayed()
        compose.onNodeWithText("Каталог").assertIsDisplayed()
    }

    @Test
    fun showcaseCanBeSwipedThroughAllThreePages() {
        compose.onNodeWithTag("app_vk").performClick()
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
        compose.onNodeWithText("Список приложений сохранён.").assertIsDisplayed()

        compose.activityRule.scenario.recreate()

        compose.onNodeWithTag("app_list").assertIsDisplayed()
        compose.onNodeWithText("ВКонтакте", useUnmergedTree = true).assertIsDisplayed()
        compose.onAllNodesWithText("Список приложений сохранён.").assertCountEquals(0)
    }
}

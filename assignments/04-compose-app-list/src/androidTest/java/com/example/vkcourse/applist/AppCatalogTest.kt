package com.example.vkcourse.applist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToKey
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
}

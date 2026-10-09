package com.example.vkcourse

import android.content.ActivityNotFoundException
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntentFailureUiTest {
    @get:Rule
    val compose = createComposeRule()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun missingDialerShowsAnErrorOnTheScreen() {
        val actions = IntentActions(context, launch = { throw ActivityNotFoundException() })
        compose.setContent { HomeworkTheme { MainScreen(actions) } }
        compose.onNodeWithTag("input").performTextReplacement("+7 (999) 123-45-67")
        compose.onNodeWithText(context.getString(R.string.dial_friend)).performScrollTo().performClick()
        compose.onNodeWithText(context.getString(R.string.error_no_dialer)).assertIsDisplayed()
    }

    @Test
    fun missingShareTargetShowsAnErrorOnTheScreen() {
        val actions = IntentActions(context, canShare = { false })
        compose.setContent { HomeworkTheme { MainScreen(actions) } }
        compose.onNodeWithTag("input").performTextReplacement("Hello")
        compose.onNodeWithText(context.getString(R.string.share_text)).performScrollTo().performClick()
        compose.onNodeWithText(context.getString(R.string.error_no_share_app)).assertIsDisplayed()
    }
}

package com.example.vkcourse

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Before
    fun recordIntents() = Intents.init()

    @After
    fun releaseIntents() = Intents.release()

    private fun click(stringId: Int) {
        compose.onNodeWithText(compose.activity.getString(stringId)).performScrollTo().performClick()
    }

    private fun error(stringId: Int) {
        compose.onNodeWithText(compose.activity.getString(stringId)).assertIsDisplayed()
    }

    @Test
    fun emptyAndWhitespaceInputsShowActionSpecificErrors() {
        listOf("", "   ").forEach { input ->
            compose.onNodeWithTag("input").performTextReplacement(input)
            click(R.string.open_second)
            error(R.string.error_empty_text)
            click(R.string.share_text)
            error(R.string.error_empty_text)
            click(R.string.dial_friend)
            error(R.string.error_empty_phone)
        }
        assertEquals(0, Intents.getIntents().size)
    }

    @Test
    fun invalidPhoneShowsErrorWithoutLaunchingAnIntent() {
        listOf("abc", "++79991234567", "(9991234567", "123#").forEach {
            compose.onNodeWithTag("input").performTextReplacement(it)
            click(R.string.dial_friend)
            error(R.string.error_invalid_phone)
        }
        assertEquals(0, Intents.getIntents().size)
    }

    @Test
    fun explicitIntentDisplaysExactTextAndSystemBackPreservesInput() {
        val text = " Привет, Android! 👋 "
        compose.onNodeWithTag("input").performTextReplacement(text)
        click(R.string.open_second)
        compose.onNodeWithText(text).assertIsDisplayed()
        val sent = Intents.getIntents().last()
        assertEquals(SecondActivity::class.java.name, sent.component?.className)
        assertEquals(text, sent.getStringExtra(SecondActivity.EXTRA_TEXT))
        Espresso.pressBack()
        compose.onNodeWithTag("input").assertTextContains(text)
    }

    @Test
    fun secondActivityReturnButtonPreservesInput() {
        compose.onNodeWithTag("input").performTextReplacement("Homework 3")
        click(R.string.open_second)
        click(R.string.back_to_main)
        compose.onNodeWithTag("input").assertTextContains("Homework 3")
    }

    @Test
    fun inputAndErrorSurviveActivityRecreation() {
        compose.onNodeWithTag("input").performTextReplacement("invalid number")
        click(R.string.dial_friend)
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("input").assertTextContains("invalid number")
        error(R.string.error_invalid_phone)
    }

}

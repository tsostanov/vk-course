package com.example.vkcourse

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecondActivityTest {
    @get:Rule
    val compose = createAndroidComposeRule<SecondActivity>()

    @Test
    fun missingExtraShowsAnErrorAndReturnButton() {
        compose.onNodeWithText(compose.activity.getString(R.string.error_missing_text)).assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.back_to_main)).assertIsDisplayed()
    }
}

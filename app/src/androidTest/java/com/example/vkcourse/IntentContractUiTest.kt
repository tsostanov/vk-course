package com.example.vkcourse

import android.content.Intent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.core.content.IntentCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntentContractUiTest {
    @get:Rule
    val compose = createComposeRule()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private fun captureFromButton(input: String, button: Int): Intent {
        var launched: Intent? = null
        val actions = IntentActions(context, launch = { launched = it }, canShare = { true })
        compose.setContent { HomeworkTheme { MainScreen(actions) } }
        compose.onNodeWithTag("input").performTextReplacement(input)
        compose.onNodeWithText(context.getString(button)).performScrollTo().performClick()
        assertNotNull(launched)
        return launched!!
    }

    @Test
    fun dialButtonUsesNormalizedNumberAndNeverCallsAutomatically() {
        val intent = captureFromButton("+7 (999) 123-45-67", R.string.dial_friend)
        assertEquals(Intent.ACTION_DIAL, intent.action)
        assertEquals("tel", intent.data?.scheme)
        assertEquals("+79991234567", intent.data?.schemeSpecificPart)
    }

    @Test
    fun shareButtonWrapsSendIntentWithPlainTextAndExactExtra() {
        val text = "Текст для отправки"
        val chooser = captureFromButton(text, R.string.share_text)
        assertEquals(Intent.ACTION_CHOOSER, chooser.action)
        assertEquals(context.getString(R.string.share_chooser), chooser.getStringExtra(Intent.EXTRA_TITLE))
        val send = IntentCompat.getParcelableExtra(chooser, Intent.EXTRA_INTENT, Intent::class.java)
        assertNotNull(send)
        assertEquals(Intent.ACTION_SEND, send!!.action)
        assertEquals("text/plain", send.type)
        assertEquals(text, send.getStringExtra(Intent.EXTRA_TEXT))
    }
}

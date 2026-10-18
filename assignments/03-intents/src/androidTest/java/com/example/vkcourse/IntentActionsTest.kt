package com.example.vkcourse

import android.content.ActivityNotFoundException
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntentActionsTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun unavailableActivitiesReturnActionSpecificMessages() {
        val actions = IntentActions(
            context,
            launch = { throw ActivityNotFoundException() },
            canShare = { true },
        )
        assertEquals(R.string.error_no_dialer, actions.dial("+7 (999) 123-45-67"))
        assertEquals(R.string.error_no_share_app, actions.share("Hello"))
        assertEquals(R.string.error_open_second, actions.openSecond("Hello"))
    }

    @Test
    fun noShareTargetIsReportedBeforeOpeningTheChooser() {
        var launches = 0
        val actions = IntentActions(context, launch = { launches++ }, canShare = { false })
        assertEquals(R.string.error_no_share_app, actions.share("Hello"))
        assertEquals(0, launches)
    }

    @Test
    fun securityFailureReturnsAVisibleMessage() {
        val actions = IntentActions(context, launch = { throw SecurityException() }, canShare = { true })
        assertEquals(R.string.error_intent_blocked, actions.dial("1234567"))
        assertEquals(R.string.error_intent_blocked, actions.share("Hello"))
    }
}

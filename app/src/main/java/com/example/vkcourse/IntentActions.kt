package com.example.vkcourse

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes

class IntentActions(
    private val context: Context,
    private val launch: (Intent) -> Unit = { context.startActivity(it) },
    private val canShare: (Intent) -> Boolean = { it.resolveActivity(context.packageManager) != null },
) {
    @StringRes
    fun openSecond(text: String): Int? {
        if (!InputValidation.hasText(text)) return R.string.error_empty_text
        // Указываем конкретную Activity и передаём ей текст из поля.
        val intent = Intent(context, SecondActivity::class.java)
            .putExtra(SecondActivity.EXTRA_TEXT, text)
        return launchSafely(intent, R.string.error_open_second)
    }

    @StringRes
    fun dial(value: String): Int? {
        if (!InputValidation.hasText(value)) return R.string.error_empty_phone
        val number = InputValidation.normalizePhone(value) ?: return R.string.error_invalid_phone
        // Открываем только набор номера. Пользователь сам решит, звонить ли дальше.
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
        return launchSafely(intent, R.string.error_no_dialer)
    }

    @StringRes
    fun share(text: String): Int? {
        if (!InputValidation.hasText(text)) return R.string.error_empty_text
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        // Сначала проверяем, есть ли приложение, которому можно передать текст.
        if (!canShare(sendIntent)) return R.string.error_no_share_app
        // Показываем системное окно, чтобы пользователь сам выбрал приложение.
        return launchSafely(
            Intent.createChooser(sendIntent, context.getString(R.string.share_chooser)),
            R.string.error_no_share_app,
        )
    }

    // При ошибке возвращаем сообщение для экрана, при успешном запуске — null.
    @StringRes
    private fun launchSafely(intent: Intent, @StringRes unavailableMessage: Int): Int? = try {
        launch(intent)
        null
    } catch (_: ActivityNotFoundException) {
        unavailableMessage
    } catch (_: SecurityException) {
        R.string.error_intent_blocked
    }
}

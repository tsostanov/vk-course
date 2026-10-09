package com.example.vkcourse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val receivedText = intent.getStringExtra(EXTRA_TEXT)

        setContent {
            HomeworkTheme {
                HomeworkPage(stringResource(R.string.second_title)) { padding ->
                    Column(
                        modifier = Modifier.padding(padding)
                            .verticalScroll(rememberScrollState()).padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        if (receivedText.isNullOrBlank()) {
                            ErrorMessage(stringResource(R.string.error_missing_text))
                        } else {
                            Text(stringResource(R.string.received_text))
                            SelectionContainer { Text(receivedText) }
                        }
                        Button(onClick = { finish() }) {
                            Text(stringResource(R.string.back_to_main))
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_TEXT = "com.example.vkcourse.extra.TEXT"
    }
}

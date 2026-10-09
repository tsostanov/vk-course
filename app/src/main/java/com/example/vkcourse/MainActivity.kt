package com.example.vkcourse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        val actions = IntentActions(this)
        setContent {
            HomeworkTheme {
                MainScreen(actions)
            }
        }
    }
}

@Composable
internal fun MainScreen(actions: IntentActions) {
    var input by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<Int?>(null) }

    HomeworkPage(stringResource(R.string.main_title)) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.main_description))
            OutlinedTextField(
                value = input,
                onValueChange = {
                    input = it
                    error = null
                },
                label = { Text(stringResource(R.string.input_label)) },
                supportingText = { Text(stringResource(R.string.input_hint)) },
                singleLine = true,
                isError = error != null,
                modifier = Modifier.fillMaxWidth().testTag("input"),
            )
            error?.let { ErrorMessage(stringResource(it)) }
            Button(
                onClick = { error = actions.openSecond(input) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.open_second))
            }
            Button(
                onClick = { error = actions.dial(input) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.dial_friend))
            }
            Button(
                onClick = { error = actions.share(input) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.share_text))
            }
        }
    }
}

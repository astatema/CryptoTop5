package com.home.cryptotop5.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.home.cryptotop5.R

/**
 * Error screen UI.
 */
@Composable
fun ErrorScreen(
    onRetry: () -> Unit
) {
    // Show the error message in a column with a retry button
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.error_message),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Button(
                onClick = onRetry
            ) {
                Text(text = "Retry")
            }
        }
    }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    ErrorScreen(
        onRetry = {}
    )
}

package com.example.fileencryptor.core.common.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.fileencryptor.core.common.R

@Composable
fun ErrorScreen(
    errorCode: Int,
    onRetryButtonClicked: (errorCode: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.error_message),
            style = MaterialTheme.typography.bodyLarge
        )
        MediumSpace()
        Button(onClick = {
            onRetryButtonClicked(errorCode)
        }) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@Preview
@Composable
fun ErrorScreenPreview() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ErrorScreen(
            0,
            onRetryButtonClicked = { errorCode: Int -> }
        )
    }
}
package com.example.fileencryptor.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fileencryptor.core.common.ui.composable.AppTopBar
import com.example.fileencryptor.core.common.ui.composable.MediumSpace

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    navigateToEncryptScreen: () -> Unit,
    navigateToDecryptScreen: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                stringResource(id = com.example.fileencryptor.core.common.R.string.screen_title_home)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            OperationListItem(
                com.example.fileencryptor.core.common.R.drawable.outline_lock_84,
                com.example.fileencryptor.core.common.R.string.encrypt
            ) {
                navigateToEncryptScreen()
            }
            MediumSpace()
            OperationListItem(
                com.example.fileencryptor.core.common.R.drawable.baseline_lock_open_84,
                com.example.fileencryptor.core.common.R.string.decrypt
            ) {
                navigateToDecryptScreen()
            }
        }
    }
}

@Composable
fun OperationListItem(
    iconResId: Int,
    titleResId: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(iconResId),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(32.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = stringResource(titleResId),
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(com.example.fileencryptor.core.common.R.drawable.baseline_keyboard_arrow_right_84),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

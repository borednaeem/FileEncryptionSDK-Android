package com.example.fileencryptor.core.common.ui.composable

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.magnifier
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fileencryptor.core.common.R

@Composable
fun FileSelectionComposable(
    modifier: Modifier = Modifier,
    isError: Boolean,
    onFileSelected: (selectedFileUri: Uri) -> Unit,
) {
    SelectionComposable(
        modifier = modifier, onSelected = onFileSelected, selectDirMode = false,
        isError = isError
    )
}

@Composable
fun DirectorySelectionComposable(
    modifier: Modifier = Modifier,
    isError: Boolean,
    onFileSelected: (selectedFileUri: Uri) -> Unit,
) {
    SelectionComposable(
        modifier = modifier,
        onSelected = onFileSelected,
        selectDirMode = true,
        isError = isError
    )
}

@Composable
private fun SelectionComposable(
    modifier: Modifier = Modifier,
    onSelected: (selectedUri: Uri) -> Unit,
    selectDirMode: Boolean,
    isError: Boolean,
) {
    val result = remember { mutableStateOf<Uri?>(null) }
    val onUriSelected: (Uri?) -> Unit = {
        result.value = it
        if (it != null) {
            onSelected(it)
        }
    }
    val fileSelectLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
            onUriSelected(it)
        }
    val dirSelectLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
            onUriSelected(it)
        }
    Card(
        modifier = Modifier
            .then(modifier),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = if (selectDirMode) R.drawable.baseline_folder_open_84 else R.drawable.outline_insert_drive_file_84),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(if (isError) MaterialTheme.colorScheme.error else Color.Gray),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(24.dp)
                )
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(id = if (selectDirMode) R.string.select_dir_button_text else R.string.select_file_button_text),
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = if (result.value == null) stringResource(id = R.string.no_file_selected) else result.value.toString(),
                        modifier = Modifier,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Button(
                    onClick = {
                        if (selectDirMode) dirSelectLauncher.launch(null) else
                            fileSelectLauncher.launch(arrayOf("*/*"))
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

                    )
                ) {
                    Text(text = stringResource(id = R.string.browse))
                }
            }
        }
    }
}
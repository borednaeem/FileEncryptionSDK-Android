@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.example.fileencryptor.feature.decrypt

import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.fileencryptor.core.common.ui.composable.AppTopBar
import com.example.fileencryptor.core.common.ui.composable.DirectorySelectionComposable
import com.example.fileencryptor.core.common.ui.composable.ErrorScreen
import com.example.fileencryptor.core.common.ui.composable.FileSelectionComposable
import com.example.fileencryptor.core.common.ui.composable.LoadingScreen
import com.example.fileencryptor.core.common.ui.composable.MediumSpace
import com.example.fileencryptor.core.common.ui.model.UiEvent
import com.example.fileencryptor.core.common.ui.model.UiOptionList
import com.example.fileencryptor.core.common.utils.getIfValidIndex
import com.example.fileencryptor.core.domain.EncryptionAlgorithm
import com.example.fileencryptor.core.domain.EncryptionMode
import com.example.fileencryptor.feature.decrypt.model.DecryptionScreenState
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DecryptScreen(viewModel: DecryptScreenViewModel) {
    val screenState by viewModel.screenState.collectAsState(DecryptionScreenState.Loading)
    val context = LocalContext.current
    LaunchedEffect(key1 = null) {
        viewModel.events.collectLatest {
            if(it is UiEvent.ShowToast) {
                val toastMessage = it.getIfNotHandled()
                if(toastMessage != null) {
                    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                stringResource(id = com.example.fileencryptor.core.common.R.string.screen_title_decrypt)
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (screenState) {
                is DecryptionScreenState.Error -> {
                    val errorCode = (screenState as DecryptionScreenState.Error).errorCode
                    ErrorScreen(errorCode) { errorCode ->
                        viewModel.onErrorScreenRetryClicked(errorCode)
                    }
                }

                is DecryptionScreenState.Loaded -> {
                    val loadedState = screenState as DecryptionScreenState.Loaded
                    EncryptScreenContent(
                        algorithmDropDownModel = loadedState.algorithmOptionList ?: UiOptionList(
                            listOf()
                        ),
                        onAlgorithmDropDownMenuItemSelect = {
                            viewModel.onAlgorithmOptionSelected(it)
                        },
                        onFileSelected = {
                            viewModel.onFileSelected(it)
                        },
                        onModeDropDownMenuItemSelect = { mode ->
                            viewModel.onAlgorithmModeSelected(mode)
                        },
                        modeDropDownModel = loadedState.modeOptionList ?: UiOptionList(
                            listOf()
                        ),
                        onConfirmClicked = {
                            viewModel.onConfirmClicked(context)
                        },
                        algorithmDropDownError = loadedState.algorithmOptionListErrorMessageResId,
                        modeDropDownError = loadedState.modeOptionListErrorMessageResId,
                        passwordError = loadedState.keyErrorMessageResId,
                        selectedFileError = loadedState.selectedFileErrorMessageResId,
                        outputDirError = loadedState.outputDirErrorMessageResId,
                        onOutputDirSelected = {
                            viewModel.onOutputDirSelected(it)
                        },
                        onPasswordTextChanged = { newText ->
                            viewModel.onPasswordTextChanged(newText)
                        }
                    )
                }

                DecryptionScreenState.Loading -> LoadingScreen()
            }
        }
    }
}

@Composable
fun EncryptScreenContent(
    algorithmDropDownModel: UiOptionList<EncryptionAlgorithm>,
    algorithmDropDownError: Int?,
    onFileSelected: (selectedFileUri: Uri) -> Unit,
    onOutputDirSelected: (outputDirUri: Uri) -> Unit,
    onAlgorithmDropDownMenuItemSelect: (EncryptionAlgorithm) -> Unit,
    modeDropDownModel: UiOptionList<EncryptionMode>,
    onModeDropDownMenuItemSelect: (EncryptionMode) -> Unit,
    onConfirmClicked: () -> Unit,
    modeDropDownError: Int?,
    passwordError: Int?,
    outputDirError: Int?,
    selectedFileError: Int?,
    onPasswordTextChanged: (String?) -> Unit,

    ) {
    var algorithmDropdownMenuBoxExpanded: Boolean by remember { mutableStateOf(false) }
    var modeDropdownMenuBoxExpanded: Boolean by remember { mutableStateOf(false) }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisibility: Boolean by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .padding(16.dp),
    ) {
        FileSelectionComposable(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp),
            isError = selectedFileError != null
        ) {
            onFileSelected(it)
        }

        MediumSpace()

        DirectorySelectionComposable(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp),
            isError = outputDirError != null
        ) {
            onOutputDirSelected(it)
        }

        MediumSpace()

        ExposedDropdownMenuBox(
            expanded = algorithmDropdownMenuBoxExpanded,
            onExpandedChange = { isExpanded ->
                algorithmDropdownMenuBoxExpanded = !algorithmDropdownMenuBoxExpanded
            },
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            val menuTitle =
                algorithmDropDownModel.list.getIfValidIndex(algorithmDropDownModel.selectedIndex)?.titleResId ?: com.example.fileencryptor.core.common.R.string.empty_string
            TextField(
                readOnly = true,
                value = stringResource(id = menuTitle),
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text(stringResource(id = com.example.fileencryptor.core.common.R.string.input_label_algorithm)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = modeDropdownMenuBoxExpanded,
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                isError = algorithmDropDownError != null
            )
            ExposedDropdownMenu(
                expanded = algorithmDropdownMenuBoxExpanded,
                onDismissRequest = {
                    algorithmDropdownMenuBoxExpanded = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                algorithmDropDownModel.list.forEachIndexed { index, dropDownItem ->
                    EncryptionAlgorithmDropDownItem(
                        dropDownItem.item,
                        dropDownItem.titleResId,
                    ) {
                        algorithmDropdownMenuBoxExpanded = false
                        onAlgorithmDropDownMenuItemSelect(it)
                    }
                    if (index < algorithmDropDownModel.list.lastIndex) {
                        Divider()
                    }
                }
            }
        }

        MediumSpace()

        ExposedDropdownMenuBox(
            expanded = modeDropdownMenuBoxExpanded,
            onExpandedChange = { isExpanded ->
                modeDropdownMenuBoxExpanded = !modeDropdownMenuBoxExpanded
            },
            modifier = Modifier
                .fillMaxWidth()

        ) {
            val menuTitle =
                modeDropDownModel.list.getIfValidIndex(modeDropDownModel.selectedIndex)?.titleResId ?: com.example.fileencryptor.core.common.R.string.empty_string
            TextField(
                readOnly = true,
                value = stringResource(id = menuTitle),
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text(text = stringResource(id = com.example.fileencryptor.core.common.R.string.input_label_mode)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = modeDropdownMenuBoxExpanded,
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                singleLine = true,
                isError = modeDropDownError != null,
            )
            ExposedDropdownMenu(
                expanded = modeDropdownMenuBoxExpanded,
                onDismissRequest = {
                    modeDropdownMenuBoxExpanded = false
                },
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                modeDropDownModel.list.forEachIndexed { index, dropDownItem ->
                    EncryptionModeDropDownItem(
                        dropDownItem.item,
                        dropDownItem.titleResId
                    ) {
                        modeDropdownMenuBoxExpanded = false
                        onModeDropDownMenuItemSelect(it)
                    }
                    if (index < modeDropDownModel.list.lastIndex) {
                        Divider()
                    }
                }
            }
        }

        MediumSpace()

        TextField(
            value = passwordText,
            onValueChange = {
                passwordText = it
                onPasswordTextChanged(it)
            },
            label = { Text(stringResource(id = com.example.fileencryptor.core.common.R.string.input_label_password)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisibility = !passwordVisibility
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisibility) com.example.fileencryptor.core.common.R.drawable.baseline_visibility_24 else
                                com.example.fileencryptor.core.common.R.drawable.baseline_visibility_off_24
                        ),
                        contentDescription = null
                    )
                }
            },
            maxLines = 1,
            isError = passwordError != null
        )

        MediumSpace()

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onConfirmClicked()
            }
        ) {
            Text(text = stringResource(id = com.example.fileencryptor.core.common.R.string.decrypt_confirm_btn))
        }
    }
}

@Composable
fun EncryptionAlgorithmDropDownItem(
    algo: EncryptionAlgorithm,
    @StringRes titleResId: Int,
    onAlgorithmDropDownMenuItemSelect: (EncryptionAlgorithm) -> Unit
) {
    DropdownMenuItem(text = {
        Text(text = stringResource(id = titleResId))
    }, onClick = {
        onAlgorithmDropDownMenuItemSelect(algo)
    })
}


@Composable
fun EncryptionModeDropDownItem(
    mode: EncryptionMode,
   @StringRes titleResId: Int,
    onModeDropDownMenuItemSelect: (EncryptionMode) -> Unit
) {
    DropdownMenuItem(text = {
        Text(text = stringResource(id = titleResId))
    }, onClick = {
        onModeDropDownMenuItemSelect(mode)
    })
}

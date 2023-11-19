package com.example.fileencryptor.feature.decrypt

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fileencryptor.core.common.R
import com.example.fileencryptor.core.common.exception.InvalidStateException
import com.example.fileencryptor.core.common.ui.model.UiEvent
import com.example.fileencryptor.core.common.ui.model.UiOptionItem
import com.example.fileencryptor.core.common.ui.model.UiOptionList
import com.example.fileencryptor.core.common.utils.getIfValidIndex
import com.example.fileencryptor.core.common.utils.throwIfDebug
import com.example.fileencryptor.core.common.utils.validIndex
import com.example.fileencryptor.core.domain.EncryptionAlgorithm
import com.example.fileencryptor.core.domain.EncryptionMode
import com.example.fileencryptor.encryptionlib.NativeLib
import com.example.fileencryptor.feature.decrypt.model.DecryptionScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DecryptScreenViewModel @Inject constructor(
    private val contentResolver: ContentResolver
) : ViewModel() {

    private val _screenState: MutableStateFlow<DecryptionScreenState> =
        MutableStateFlow(DecryptionScreenState.Loading)
    val screenState: Flow<DecryptionScreenState> = _screenState

    private val _events: MutableSharedFlow<UiEvent<out Any>> = MutableSharedFlow(
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
        extraBufferCapacity = 20
    )
    val events: Flow<UiEvent<out Any>> = _events

    init {
        loadOptions()
    }

    private fun loadOptions() {
        viewModelScope.launch {
            _screenState.update {
                DecryptionScreenState.Loaded(
                    selectedFile = null,
                    key = null,
                    algorithmOptionList = UiOptionList(
                        list = listOf(
                            UiOptionItem(
                                item = EncryptionAlgorithm.AES,
                                R.string.aes
                            ),
                            UiOptionItem(
                                item = EncryptionAlgorithm.Camellia,
                                R.string.camellia
                            ),
                            UiOptionItem(
                                item = EncryptionAlgorithm.Blowfish,
                                R.string.blowfish
                            )
                        ),
                        selectedIndex = 0
                    ),
                    modeOptionList = UiOptionList(
                        list = listOf(
                            UiOptionItem(
                                item = EncryptionMode.CBC,
                                R.string.cbc
                            ),
                            UiOptionItem(
                                item = EncryptionMode.CFB,
                                R.string.cfb
                            ),
                            UiOptionItem(
                                item = EncryptionMode.ECB,
                                R.string.ecb
                            ),
                            UiOptionItem(
                                item = EncryptionMode.OFB,
                                R.string.ofb
                            )
                        ),
                        selectedIndex = null
                    )
                )
            }
        }
    }

    fun onAlgorithmOptionSelected(encryptionAlgorithm: EncryptionAlgorithm) {
        val oldState = _screenState.value
        if (oldState is DecryptionScreenState.Loaded) {
            val selectedIndex = oldState.algorithmOptionList?.list?.map {
                it.item
            }?.indexOf(encryptionAlgorithm)
            if (selectedIndex == -1) {
                throwIfDebug(InvalidStateException())
            } else {
                _screenState.update {
                    oldState.copy(
                        algorithmOptionList = oldState.algorithmOptionList?.copy(
                            selectedIndex = selectedIndex
                        ),
                        algorithmOptionListErrorMessageResId = null
                    )
                }
            }
        } else {
            throwIfDebug(
                InvalidStateException()
            )
        }
    }

    fun onErrorScreenRetryClicked(errorCode: Int) {
        loadOptions()
    }

    fun onFileSelected(fileUri: Uri) {
        val oldState = _screenState.value
        if (oldState is DecryptionScreenState.Loaded) {
            _screenState.update {
                oldState.copy(
                    selectedFile = fileUri,
                    selectedFileErrorMessageResId = null
                )
            }
        } else {
            throwIfDebug(
                InvalidStateException()
            )
        }
    }

    fun onAlgorithmModeSelected(mode: EncryptionMode) {
        val oldState = _screenState.value
        if (oldState is DecryptionScreenState.Loaded) {
            val selectedIndex = oldState.modeOptionList?.list?.map {
                it.item
            }?.indexOf(mode)
            if (selectedIndex == -1) {
                throwIfDebug(InvalidStateException())
            } else {
                _screenState.update {
                    oldState.copy(
                        modeOptionList = oldState.modeOptionList?.copy(
                            selectedIndex = selectedIndex
                        ),
                        modeOptionListErrorMessageResId = null
                    )
                }
            }
        } else {
            throwIfDebug(
                InvalidStateException()
            )
        }
    }

    fun onConfirmClicked(context: Context) {
        val stateAfterValidation =
            validateInputs(_screenState.value as DecryptionScreenState.Loaded)

        _screenState.update { stateAfterValidation }

        if (!stateAfterValidation.hasAnyError) {
            _screenState.update { DecryptionScreenState.Loading }

            val algo =
                stateAfterValidation.algorithmOptionList?.list?.getIfValidIndex(stateAfterValidation.algorithmOptionList.selectedIndex)?.item?.name
            val mode =
                stateAfterValidation.modeOptionList?.list?.getIfValidIndex(stateAfterValidation.modeOptionList.selectedIndex)?.item?.name

            // TODO: Rewrite all of this

            val afd: ParcelFileDescriptor? = contentResolver.openFile(stateAfterValidation.selectedFile!!, "r", CancellationSignal())
            val f: Int = afd!!.detachFd()

            val outputDirDocument = DocumentFile.fromTreeUri(context, stateAfterValidation.outputDirectory!!)
            val outputDocumentFile = outputDirDocument!!.createFile("file", "newFile")
            val ofd = contentResolver.openFile(outputDocumentFile!!.uri, "rw", CancellationSignal())

            val error = NativeLib().encrypt(
                algo = algo ?: "",
                mode = mode ?: "",
                key = stateAfterValidation.key ?: "",
                inputFileDescriptor = f,
                outputFileDescriptor = ofd!!.detachFd()
            )

            if(error != 0) {
                _events.tryEmit(UiEvent.ShowToast(com.example.fileencryptor.core.common.R.string.encryption_failed))
            } else {
                _events.tryEmit(UiEvent.ShowToast(com.example.fileencryptor.core.common.R.string.encryption_success))
            }

            _screenState.update { stateAfterValidation }
        }
    }

    private fun validateInputs(uiState: DecryptionScreenState.Loaded): DecryptionScreenState.Loaded {
        var newState: DecryptionScreenState.Loaded = uiState
        newState = if (uiState.selectedFile == null) {
            newState.copy(selectedFileErrorMessageResId = com.example.fileencryptor.core.common.R.string.error_message_file_not_selected)
        } else {
            newState.copy(selectedFileErrorMessageResId = null)
        }

        newState = if (uiState.outputDirectory == null) {
            newState.copy(outputDirErrorMessageResId = com.example.fileencryptor.core.common.R.string.error_message_output_dir_not_selected)
        } else {
            newState.copy(outputDirErrorMessageResId = null)
        }

        newState =
            if (uiState.algorithmOptionList?.list?.validIndex(uiState.algorithmOptionList.selectedIndex) == true) {
                newState.copy(algorithmOptionListErrorMessageResId = null)
            } else {
                newState.copy(algorithmOptionListErrorMessageResId = com.example.fileencryptor.core.common.R.string.error_message_algo_not_selected)
            }

        newState =
            if (uiState.modeOptionList?.list?.validIndex(uiState.modeOptionList.selectedIndex) == true) {
                newState.copy(modeOptionListErrorMessageResId = null)
            } else {
                newState.copy(modeOptionListErrorMessageResId = com.example.fileencryptor.core.common.R.string.error_message_mode_not_selected)
            }

        newState = if (uiState.key != null) {
            newState.copy(keyErrorMessageResId = null)
        } else {
            newState.copy(keyErrorMessageResId = com.example.fileencryptor.core.common.R.string.error_message_password_not_selected)
        }

        return newState
    }

    fun onOutputDirSelected(outputDirUri: Uri) {
        val oldState = _screenState.value
        if (oldState is DecryptionScreenState.Loaded) {
            _screenState.update {
                oldState.copy(
                    outputDirectory = outputDirUri,
                    outputDirErrorMessageResId = null
                )
            }
        } else {
            throwIfDebug(
                InvalidStateException()
            )
        }
    }

    fun onPasswordTextChanged(newText: String?) {
        val oldState = _screenState.value
        if (oldState is DecryptionScreenState.Loaded) {
            _screenState.update {
                oldState.copy(
                    key = newText,
                    keyErrorMessageResId = if(newText.isNullOrBlank()) com.example.fileencryptor.core.common.R.string.error_message_password_not_selected else null
                )
            }
        } else {
            throwIfDebug(
                InvalidStateException()
            )
        }
    }
}
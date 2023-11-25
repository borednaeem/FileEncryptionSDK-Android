package com.example.fileencryptor.feature.decrypt.model

import android.net.Uri
import androidx.annotation.StringRes
import com.example.fileencryptor.core.common.ui.model.UiOptionList
import com.example.fileencryptor.core.domain.EncryptionAlgorithm
import com.example.fileencryptor.core.domain.EncryptionMode

sealed class DecryptionScreenState {
    data object Loading : DecryptionScreenState()
    data class Loaded(
        val selectedFile: Uri? = null,
        @StringRes val selectedFileErrorMessageResId: Int? = null,
        val outputDirectory: Uri? = null,
        @StringRes val outputDirErrorMessageResId: Int? = null,
        val algorithmOptionList: UiOptionList<EncryptionAlgorithm>? = null,
        @StringRes val algorithmOptionListErrorMessageResId: Int? = null,
        val modeOptionList: UiOptionList<EncryptionMode>? = null,
        @StringRes val modeOptionListErrorMessageResId: Int? = null,
        val key: String? = null,
        @StringRes val keyErrorMessageResId: Int? = null
    ) : DecryptionScreenState() {
        val hasAnyError: Boolean get() = selectedFileErrorMessageResId != null ||
                outputDirErrorMessageResId != null ||
                algorithmOptionListErrorMessageResId != null ||
                modeOptionListErrorMessageResId != null ||
                keyErrorMessageResId != null
    }

    data class Error(val errorCode: Int) : DecryptionScreenState()
}
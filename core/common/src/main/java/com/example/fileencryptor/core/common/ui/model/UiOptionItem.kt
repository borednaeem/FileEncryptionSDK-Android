package com.example.fileencryptor.core.common.ui.model

import androidx.annotation.StringRes

data class UiOptionItem<T>(
    val item: T,
    @StringRes val titleResId: Int
)
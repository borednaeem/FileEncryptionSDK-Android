package com.example.fileencryptor.core.common.ui.model

data class UiOptionList<T>(
    val list: List<UiOptionItem<T>>,
    val selectedIndex: Int? = null
)
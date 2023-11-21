package com.example.fileencryptor.core.common.ui.model

sealed class UiEvent<T>(
    private val data: T
) {
    private var isHandled: Boolean = false
    fun getIfNotHandled(): T? = if(!isHandled) {
        isHandled = true
        data
    } else null
    class ShowToast(toastMessageResId: Int) : UiEvent<Int>(toastMessageResId)
}
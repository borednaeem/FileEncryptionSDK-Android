package com.example.fileencryptor.core.common.utils

import com.example.easylogging.logDebug
import com.example.fileencryptor.core.common.BuildConfig
import java.lang.Error

fun throwIfDebug(e: Throwable) {
    if (BuildConfig.DEBUG) {
        throw e
    } else {
        logDebug(e)
    }
}
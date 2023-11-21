package com.example.fileencryptor.core.common.utils

import com.example.fileencryptor.core.common.BuildConfig
import com.example.fileencryptor.logging.logDebug


fun throwIfDebug(e: Throwable) {
    if (BuildConfig.DEBUG) {
        throw e
    } else {
        logDebug(e)
    }
}
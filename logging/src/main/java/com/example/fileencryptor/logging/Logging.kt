package com.example.fileencryptor.logging

import android.util.Log

fun logDebug(message: String) {
    val callerClassName = Exception().stackTrace[1].className
    Log.d(callerClassName, message)
}

fun logDebug(vararg args: Any) {
    val message = StringBuilder().apply {
        for(a in args) append(" $a")
    }.toString()
    logDebug(message)
}
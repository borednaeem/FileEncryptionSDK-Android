package com.example.fileencryptor.encryptionlib

import android.os.ParcelFileDescriptor
import java.io.FileDescriptor

class NativeLib {

    /**
     * A native method that is implemented by the 'encryptionlib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun initOpenSSL()

    external fun encrypt(
        algo: String,
        mode: String,
        key: String,
        inputFileDescriptor: Int,
        outputFileDescriptor: Int
    ): Int

    external fun decrypt(
        algo: String,
        mode: String,
        key: String,
        inputFileDescriptor: Int,
        outputFileDescriptor: Int
    ): Int

    companion object {
        // Used to load the 'encryptionlib' library on application startup.
        init {
            System.loadLibrary("crypto")
            System.loadLibrary("encryptionlib")
        }
    }
}
package com.example.fileencryptor.encryptionlib

class NativeLib {

    /**
     * A native method that is implemented by the 'encryptionlib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun initOpenSSL()

    companion object {
        // Used to load the 'encryptionlib' library on application startup.
        init {
            System.loadLibrary("crypto")
            System.loadLibrary("encryptionlib")
        }
    }
}
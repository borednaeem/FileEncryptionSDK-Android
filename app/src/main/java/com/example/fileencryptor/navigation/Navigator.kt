package com.example.fileencryptor.navigation

import androidx.navigation.NavController
import com.example.feature.encrypt.navigation.navigateToEncryptScreen
import com.example.fileencryptor.feature.decrypt.navigation.navigateToDecryptScreen
import com.example.fileencryptor.feature.home.navigation.navigateToHomeScreen

interface Navigator {
    fun navigateToHomeScreen()
    fun navigateToEncryptScreen()
    fun navigateToDecryptScreen()
}

class NavigatorImpl(
    private val navController: NavController
): Navigator {
    override fun navigateToHomeScreen() {
        navController.navigateToHomeScreen()
    }

    override fun navigateToEncryptScreen() {
        navController.navigateToEncryptScreen()
    }

    override fun navigateToDecryptScreen() {
        navController.navigateToDecryptScreen()
    }

}
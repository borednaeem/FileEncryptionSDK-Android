package com.example.fileencryptor.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.feature.encrypt.navigation.EncryptScreen
import com.example.feature.encrypt.navigation.navigateToEncryptScreen
import com.example.fileencryptor.feature.decrypt.navigation.DecryptScreen
import com.example.fileencryptor.feature.decrypt.navigation.navigateToDecryptScreen
import com.example.fileencryptor.feature.home.navigation.HomeScreen
import com.example.fileencryptor.feature.home.navigation.homeScreenNavigationRoute

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun AppNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = homeScreenNavigationRoute,
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        HomeScreen(
            navigateToEncryptScreen = {
                navHostController.navigateToEncryptScreen()
            },
            navigateToDecryptScreen = {
                navHostController.navigateToDecryptScreen()
            },
        )
        EncryptScreen()
        DecryptScreen()
    }
}

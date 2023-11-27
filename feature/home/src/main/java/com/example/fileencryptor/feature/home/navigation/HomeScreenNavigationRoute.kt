package com.example.fileencryptor.feature.home.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.fileencryptor.feature.home.HomeScreen
import com.example.fileencryptor.feature.home.HomeScreenViewModel

const val homeScreenNavigationRoute = "home"

fun NavController.navigateToHomeScreen(navOptions: NavOptions? = null) {
    this.navigate(homeScreenNavigationRoute, navOptions)
}

fun NavGraphBuilder.HomeScreen(
    navigateToEncryptScreen: () -> Unit,
    navigateToDecryptScreen: () -> Unit,
) {
    composable(
        route = homeScreenNavigationRoute,
    ) {
        HomeScreenRoute(
            navigateToEncryptScreen,
            navigateToDecryptScreen
        )
    }
}

@Composable
internal fun HomeScreenRoute(
    navigateToEncryptScreen: () -> Unit,
    navigateToDecryptScreen: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
}

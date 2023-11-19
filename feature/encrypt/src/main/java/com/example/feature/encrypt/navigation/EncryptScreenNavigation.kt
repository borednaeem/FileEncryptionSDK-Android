package com.example.feature.encrypt.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.feature.encrypt.EncryptScreen
import com.example.feature.encrypt.EncryptScreenViewModel

const val encryptScreenNavigationRoute = "encrypt"

fun NavController.navigateToEncryptScreen(navOptions: NavOptions? = null) {
    this.navigate(encryptScreenNavigationRoute, navOptions)
}

fun NavGraphBuilder.EncryptScreen() {
    composable(
        route = encryptScreenNavigationRoute,
    ) {
        EncryptScreenRoute()
    }
}

@Composable
internal fun EncryptScreenRoute(
    viewModel: EncryptScreenViewModel = hiltViewModel(),
) {
    EncryptScreen(
        viewModel = viewModel
    )
}

package com.example.fileencryptor.feature.decrypt.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.fileencryptor.feature.decrypt.DecryptScreen
import com.example.fileencryptor.feature.decrypt.DecryptScreenViewModel

const val decryptScreenNavigationRoute = "decrypt"

fun NavController.navigateToDecryptScreen(navOptions: NavOptions? = null) {
    this.navigate(decryptScreenNavigationRoute, navOptions)
}

fun NavGraphBuilder.DecryptScreen() {
    composable(
        route = decryptScreenNavigationRoute,
    ) {
        DecryptScreenRoute()
    }
}

@Composable
internal fun DecryptScreenRoute(
    viewModel: DecryptScreenViewModel = hiltViewModel(),
) {
    DecryptScreen(
        viewModel = viewModel
    )
}

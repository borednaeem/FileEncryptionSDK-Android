package com.example.fileencryptor.core.common.ui.composable


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.fileencryptor.core.common.R


@Composable
fun ExtraSmallSpace() {
    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.extra_small_space).value.dp))
}

@Composable
fun SmallSpace() {
    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.small_space).value.dp))
}

@Composable
fun MediumSpace() {
    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.medium_space).value.dp))
}

@Composable
fun LargeSpace() {
    Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.large_space).value.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(screenTitle: String) {
    TopAppBar(
        title = {
            Text(text = screenTitle)
        }
    )
}



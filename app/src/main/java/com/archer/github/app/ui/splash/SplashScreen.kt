package com.archer.github.app.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.archer.github.app.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(gotoMainScreen: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(Unit) {
            delay(2000)
            gotoMainScreen()
        }

        Text(stringResource(R.string.splash_hint), style = MaterialTheme.typography.titleLarge)
    }
}

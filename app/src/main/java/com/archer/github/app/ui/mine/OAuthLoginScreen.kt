package com.archer.github.app.ui.mine

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.archer.github.app.R
import com.archer.github.app.common.Constant

@Composable
fun OAuthLoginScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(onClick = {
            CustomTabsIntent.Builder().build().apply {
                launchUrl(context, Constant.OAUTH_LOGIN_URL)
            }
        }) {
            Text(text = stringResource(R.string.login_oauth_title))
        }
    }
}

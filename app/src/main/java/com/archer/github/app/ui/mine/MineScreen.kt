package com.archer.github.app.ui.mine

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.archer.github.app.GithubApp
import com.archer.github.app.utils.LogUtil

@Composable
fun MineScreen() {
    val loginState by GithubApp.appViewModel.loginState.collectAsState()
    LogUtil.d("MineScreen->loginState.isLoggedIn=${loginState.isLoggedIn}, loginState.user=${loginState.user}")
    if (loginState.isLoggedIn && loginState.user != null) {
        ProfileScreen()
    } else {
        OAuthLoginScreen()
    }
}

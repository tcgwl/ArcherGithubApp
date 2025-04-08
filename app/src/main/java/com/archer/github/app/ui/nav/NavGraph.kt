package com.archer.github.app.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.archer.github.app.ui.main.MainScreen
import com.archer.github.app.ui.splash.SplashScreen

@Composable
fun NavGraph(navController: NavHostController, onFinish: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = Route.SPLASH
    ) {
        composable(Route.SPLASH) {
            SplashScreen {
                navController.navigate(Route.MAIN)
            }
        }
        composable(Route.MAIN) {
            MainScreen(onFinish)
        }
    }
}

object Route {
    const val SPLASH = "splash"
    const val MAIN = "main"
}

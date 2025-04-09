package com.archer.github.app.ui.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.archer.github.app.GithubApp
import com.archer.github.app.ui.main.MainScreen
import com.archer.github.app.ui.mine.MyRepoScreen
import com.archer.github.app.ui.splash.SplashScreen
import com.archer.github.app.utils.LocalNavHostController
import com.archer.github.app.utils.LogUtil
import com.archer.github.app.R
import com.archer.github.app.common.Constant
import com.archer.github.app.ui.search.SearchScreen
import com.archer.github.app.ui.web.WebPage
import java.net.URLDecoder

@Composable
fun NavGraph(navController: NavHostController, onFinish: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observable = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                LogUtil.d("NavGraph->ON_RESUME")
                GithubApp.appViewModel.checkLoginState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observable)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observable)
        }
    }

    CompositionLocalProvider(LocalNavHostController provides navController) {
        NavHost(
            navController = navController,
            startDestination = Route.SPLASH.route
        ) {
            composable(Route.SPLASH.route) {
                SplashScreen {
                    navController.navigate(Route.MAIN.route)
                }
            }
            composable(Route.MAIN.route) {
                MainScreen {
                    onFinish()
                }
            }
            composable(Route.MY_REPO.route) {
                MyRepoScreen()
            }
            composable(Route.SEARCH.route) {
                SearchScreen()
            }
            composable(
                "${Route.WEB.route}/{url}", arguments = listOf(
                    navArgument("url") {
                        type = NavType.StringType
                    }
                )
            ) {
                val url = it.arguments?.getString("url") ?: ""
                WebPage(Constant.GITHUB_BASE_URL.plus(URLDecoder.decode(url, "UTF-8")))
            }
        }
    }
}

sealed class Destination(
    val route: String,
    @StringRes val tabName: Int,
    val icon: ImageVector
) {
    data object HomeScreen : Destination(
        route = Route.HOME.route,
        tabName = R.string.main_tab_home,
        icon = Icons.Filled.Home
    )

    data object MineScreen : Destination(
        route = Route.MINE.route,
        tabName = R.string.main_tab_mine,
        icon = Icons.Filled.Person
    )
}

enum class Route(val route: String) {
    SPLASH("splash"),
    MAIN("main"),
    HOME("home"),
    MINE("mine"),
    MY_REPO("myRepo"),
    SEARCH("search"),
    WEB("web")
}

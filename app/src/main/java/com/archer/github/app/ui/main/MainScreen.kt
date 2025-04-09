package com.archer.github.app.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.archer.github.app.R
import com.archer.github.app.base.BaseApp
import com.archer.github.app.ui.home.HomeScreen
import com.archer.github.app.ui.mine.MineScreen
import com.archer.github.app.ui.nav.Destination
import com.archer.github.app.utils.ToastUtil
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val tabList = listOf(
        Destination.HomeScreen, Destination.MineScreen
    )
    val pagerState = rememberPagerState { tabList.size }
    val coroutineScope = rememberCoroutineScope()

    var lastClickTime = remember { 0L }
    BackHandler {
        if (System.currentTimeMillis() - lastClickTime > 2000) {
            lastClickTime = System.currentTimeMillis()
            ToastUtil.showShort(BaseApp.appContext.getString(R.string.main_exit_hint))
        } else {
            onFinish()
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.selectTab(pagerState.currentPage)
    }

    LaunchedEffect(viewModel.selectedTab.value) {
        pagerState.animateScrollToPage(viewModel.selectedTab.value)
    }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.primary
            ) {
                tabList.forEachIndexed { index, destination ->
                    BottomNavigationItem(
                        selected = viewModel.selectedTab.value == index,
                        onClick = {
                            viewModel.selectTab(index)
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = {
                            Icon(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(bottom = 4.dp),
                                imageVector = destination.icon,
                                contentDescription = stringResource(destination.tabName)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(destination.tabName),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedContentColor = MaterialTheme.colorScheme.onPrimary.copy(0.3f),
                    )
                }
            }
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(it)
        ) { page ->
            when (page) {
                0 -> HomeScreen()
                1 -> MineScreen()
            }
        }
    }
}

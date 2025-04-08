package com.archer.github.app.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.archer.github.app.logic.model.NavigationItem
import com.archer.github.app.ui.home.HomeScreen
import com.archer.github.app.ui.mine.MineScreen
import com.archer.github.app.ui.search.SearchScreen
import com.archer.github.app.utils.ToastUtil

@Composable
fun MainScreen(onFinish: () -> Unit) {
    val tabs = listOf(
        NavigationItem("首页", Icons.Filled.Home),
        NavigationItem("搜索", Icons.Filled.Search),
        NavigationItem("我的", Icons.Filled.Person)
    )
    var selectedTab by remember { mutableIntStateOf(0) }

    var lastClickTime = remember { 0L }
    BackHandler {
        if (System.currentTimeMillis() - lastClickTime > 2000) {
            lastClickTime = System.currentTimeMillis()
            ToastUtil.showShort("再按一次退出")
        } else {
            onFinish()
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, navigationItem ->
                    BottomNavigationItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = navigationItem.icon,
                                contentDescription = navigationItem.title,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(bottom = 4.dp)
                            )
                        },
                        label = {
                            Text(
                                text = navigationItem.title,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedContentColor = MaterialTheme.colorScheme.onPrimary.copy(0.3f),
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (selectedTab) {
                0 -> HomeScreen()
                1 -> SearchScreen()
                2 -> MineScreen()
            }
        }
    }
}

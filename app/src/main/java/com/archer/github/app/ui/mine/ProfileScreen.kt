package com.archer.github.app.ui.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.archer.github.app.GithubApp
import com.archer.github.app.R
import com.archer.github.app.ui.nav.Route
import com.archer.github.app.ui.widgets.CoilImage
import com.archer.github.app.ui.widgets.CustomDialog
import com.archer.github.app.utils.LocalNavHostController

@Composable
fun ProfileScreen() {
    val navController = LocalNavHostController.current
    val loginState by GithubApp.appViewModel.loginState.collectAsState()

    loginState.user?.let { user ->
        val logoffPopup = remember { mutableStateOf(false) }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.primary)
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CoilImage(
                            data = user.avatarUrl,
                            size = DpSize(72.dp, 72.dp)
                        )
                        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                            Text(
                                text = user.login,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = user.bio,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = user.email,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    ListItemWithIcon(
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = stringResource(R.string.mine_repo)
                            )
                        },
                        title = stringResource(id = R.string.mine_repo),
                        value = {
                            Text(
                                text = "${user.publicRepos}",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 15.sp
                            )
                        }
                    ) {
                        navController.navigate(Route.MY_REPO.route)
                    }

                    Divider(
                        Modifier.height(5.dp),
                        color = LocalContentColor.current.copy(0.1f)
                    )

                    ListItemWithIcon(
                        icon = {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = stringResource(R.string.accessibility_logout)
                            )
                        },
                        title = stringResource(id = R.string.login_out)
                    ) {
                        logoffPopup.value = true
                    }
                }
            }
        }

        if (logoffPopup.value) {
            CustomDialog(
                content = {
                    Text(stringResource(R.string.dialog_logout_content))
                },
                onConfirm = {
                    GithubApp.appViewModel.logoff()
                    logoffPopup.value = false
                }
            ) {
                logoffPopup.value = false
            }
        }
    }
}

@Composable
fun ListItemWithIcon(
    icon: @Composable () -> Unit,
    title: String,
    value: @Composable (() -> Unit)? = null,
    onItemClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onItemClick.invoke() }
            .padding(horizontal = 10.dp)
            .height(60.dp)
    ) {
        icon()
        Text(text = title, modifier = Modifier.padding(start = 10.dp))
        Spacer(modifier = Modifier.weight(1f))
        value?.invoke()
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "KeyboardArrowRight",
            tint = LocalContentColor.current.copy(alpha = 0.5f),
        )
    }
}

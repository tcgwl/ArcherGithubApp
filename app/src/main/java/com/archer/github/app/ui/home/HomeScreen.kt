package com.archer.github.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.archer.github.app.R
import com.archer.github.app.logic.model.LANGUAGE_DROP_DOWN
import com.archer.github.app.logic.model.SINCE_DROP_DOWN
import com.archer.github.app.ui.nav.Route
import com.archer.github.app.ui.widgets.CustomDialog
import com.archer.github.app.ui.widgets.CustomDropDownMenu
import com.archer.github.app.ui.widgets.CustomLoadingView
import com.archer.github.app.ui.widgets.TrendRepoItem
import com.archer.github.app.ui.widgets.TitleBar
import com.archer.github.app.utils.LocalNavHostController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {

    val navController = LocalNavHostController.current
    val lazyListState = rememberLazyListState()
    val repoList by viewModel.repoList.collectAsState()
    val errorPopup = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getTrendRepoList()
        viewModel.repoList.collectLatest {
            lazyListState.scrollToItem(0)
        }
        viewModel.errorState.collectLatest {
            errorPopup.value = it.message
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TitleBar(
            title = stringResource(R.string.main_tab_home),
            menu = {
                Icon(
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            navController.navigate(Route.SEARCH.route)
                        },
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_title),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        )

        Divider(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp))

        Row(modifier = Modifier.fillMaxWidth().height(56.dp)) {
            CustomDropDownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                SINCE_DROP_DOWN
            ) {
                viewModel.getTrendRepoList(since = it)
            }
            Divider(modifier = Modifier
                .fillMaxHeight()
                .width(1.dp))
            CustomDropDownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                LANGUAGE_DROP_DOWN
            ) {
                viewModel.getTrendRepoList(language = it)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = lazyListState
        ) {
            repoList?.let { data ->
                items(data, key = { it.url }) {
                    TrendRepoItem(repo = it)
                }
            }
        }
    }

    if (viewModel.loadingState.collectAsState().value) {
        CustomLoadingView()
    }

    if (errorPopup.value.isNotEmpty()) {
        CustomDialog(
            confirmText = stringResource(R.string.dialog_button_retry),
            content = {
                Text(errorPopup.value)
            },
            onConfirm = {
                viewModel.getTrendRepoList()
                errorPopup.value = ""
            }
        ) {
            errorPopup.value = ""
        }
    }
}

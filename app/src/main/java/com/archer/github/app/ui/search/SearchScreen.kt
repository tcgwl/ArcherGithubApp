package com.archer.github.app.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.archer.github.app.R
import com.archer.github.app.ui.widgets.CustomDialog
import com.archer.github.app.ui.widgets.SearchRepoItem
import com.archer.github.app.ui.widgets.TitleBar
import com.archer.github.app.utils.LocalNavHostController
import com.archer.github.app.utils.LogUtil
import com.archer.github.app.utils.ToastUtil
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val navController = LocalNavHostController.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val lazyListState = rememberLazyListState()
    val searchRepoList = viewModel.searchRepoResult.collectAsState()
    val isRefreshing = viewModel.loadingState.collectAsState().value
    val errorPopup = remember { mutableStateOf("") }
    var keywords by rememberSaveable { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        viewModel.errorState.collectLatest {
            errorPopup.value = it.message
        }
    }

    // 上拉加载更多监听
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastVisibleItemIndex ->
                LogUtil.d("lastVisibleItemIndex=$lastVisibleItemIndex, searchRepoList.lastIndex=${searchRepoList.value.lastIndex}, viewModel.isLoadingMore=${viewModel.isLoadingMore}")
                if (lastVisibleItemIndex == searchRepoList.value.lastIndex && !viewModel.isLoadingMore) {
                    viewModel.loadMore()
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            val searchAble = keywords.trim().isNotEmpty()
            TitleBar(
                menu = {
                    Icon(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable(enabled = searchAble) {
                                if (keywords.trim().isEmpty()) {
                                    ToastUtil.showShort(context.getString(R.string.search_hint))
                                } else {
                                    keyboardController?.hide()
                                    viewModel.refresh(keywords)
                                }
                            },
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary.copy(if (searchAble) 1f else 0.4f)
                    )
                }
            ) {
                navController.popBackStack()
            }
            TextField(
                modifier = Modifier
                    .padding(horizontal = 50.dp)
                    .focusRequester(focusRequester),
                value = keywords,
                singleLine = true,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_hint),
                        color = MaterialTheme.colorScheme.onPrimary.copy(0.5f)
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary.copy(0.5f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions {
                    if (keywords.trim().isEmpty()) {
                        ToastUtil.showShort(context.getString(R.string.search_hint))
                    } else {
                        keyboardController?.hide()
                        viewModel.refresh(keywords)
                    }
                },
                onValueChange = { value -> keywords = value })
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                if (keywords.trim().isNotEmpty()) {
                    viewModel.refresh(keywords)
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = lazyListState
            ) {
                items(searchRepoList.value, key = { it.id }) {
                    SearchRepoItem(repo = it)
                }

                // 上拉加载指示器
                if (viewModel.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (viewModel.searchRepoResult.value.isNotEmpty() && viewModel.isLastPage) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.list_load_more_no_data),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    if (errorPopup.value.isNotEmpty()) {
        CustomDialog(
            confirmText = stringResource(R.string.dialog_button_retry),
            content = {
                Text(errorPopup.value)
            },
            onConfirm = {
                viewModel.refresh(keywords)
                errorPopup.value = ""
            }
        ) {
            errorPopup.value = ""
        }
    }
}

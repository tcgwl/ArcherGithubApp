package com.archer.github.app.ui.mine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.archer.github.app.R
import com.archer.github.app.ui.widgets.CustomDialog
import com.archer.github.app.ui.widgets.CustomLoadingView
import com.archer.github.app.ui.widgets.SearchRepoItem
import com.archer.github.app.ui.widgets.TitleBar
import com.archer.github.app.utils.LocalNavHostController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyRepoScreen(viewModel: MineViewModel = hiltViewModel()) {

    val navController = LocalNavHostController.current
    val lazyListState = rememberLazyListState()
    val userRepoList = viewModel.userRepoList.collectAsState().value
    val errorPopup = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getUserRepoList()
        viewModel.errorState.collectLatest {
            errorPopup.value = it.message
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TitleBar(title = stringResource(R.string.mine_repo)) {
            navController.popBackStack()
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = lazyListState
        ) {
            userRepoList?.let { data ->
                items(data, key = { it.id }) {
                    SearchRepoItem(repo = it, isMine = true)
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
                viewModel.getUserRepoList()
                errorPopup.value = ""
            }
        ) {
            errorPopup.value = ""
        }
    }
}

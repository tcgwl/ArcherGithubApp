package com.archer.github.app.ui.mine

import androidx.annotation.VisibleForTesting
import com.archer.github.app.base.BaseViewModel
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.local.UserDao
import com.archer.github.app.logic.model.ErrorType
import com.archer.github.app.logic.model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor() : BaseViewModel() {

    private val _userRepoList: MutableStateFlow<List<Repository>?> = MutableStateFlow(null)
    val userRepoList = _userRepoList.asStateFlow()

    @VisibleForTesting
    var repoProvider: () -> GithubRepository = { GithubRepository }

    fun getUserRepoList() {
        UserDao.getAccessToken()?.let { token ->
            launchWithErrorHandler {
                _loadingState.value = true

                val response = repoProvider().getUserRepoList(token)
                response.apply {
                    onSuccess {
                        _userRepoList.value = it
                    }

                    onFailure {
                        handleApiError(ErrorType.NETWORK_ERROR)
                    }
                }

                _loadingState.value = false
            }
        }
    }
}

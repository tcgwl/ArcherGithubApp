package com.archer.github.app.ui.mine

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.local.UserDao
import com.archer.github.app.logic.model.ErrorType
import com.archer.github.app.logic.model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor() : ViewModel() {

    private val _loadingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _userRepoList: MutableStateFlow<List<Repository>?> = MutableStateFlow(null)
    val userRepoList = _userRepoList.asStateFlow()

    private val _errorState: MutableSharedFlow<ErrorType> = MutableSharedFlow()
    val errorState = _errorState

    @VisibleForTesting
    var repoProvider: () -> GithubRepository = { GithubRepository }

    fun getUserRepoList() {
        UserDao.getAccessToken()?.let {
            val exception = CoroutineExceptionHandler { _, _ ->
                handleApiError(ErrorType.NETWORK_EXCEPTION)
            }

            viewModelScope.launch(exception) {
                _loadingState.value = true
                val response = repoProvider().getUserRepoList(it)
                response.apply {
                    onSuccess {
                        _userRepoList.value = it
                        _loadingState.value = false
                    }

                    onFailure {
                        handleApiError(ErrorType.NETWORK_ERROR)
                        _loadingState.value = false
                    }
                }
            }
        }
    }

    private fun handleApiError(error: ErrorType) {
        viewModelScope.launch {
            _errorState.emit(error)
        }
    }
}

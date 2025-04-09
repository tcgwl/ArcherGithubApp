package com.archer.github.app

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.local.UserDao
import com.archer.github.app.logic.model.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val repo: GithubRepository = GithubRepository
) : ViewModel() {

    @VisibleForTesting
    val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    fun checkLoginState() {
        UserDao.getAccessToken()?.let { token ->
            viewModelScope.launch {
                repo.getUserInfo(token).apply {
                    onSuccess {
                        _loginState.value = _loginState.value.copy(
                            isLoggedIn = true, user = it
                        )
                    }

                    onFailure {
                        _loginState.value = _loginState.value.copy(isLoggedIn = false)
                    }
                }
            }
        } ?: run {
            _loginState.value = _loginState.value.copy(isLoggedIn = false)
        }
    }

    fun logoff() {
        UserDao.removeAccessToken()
        _loginState.value = _loginState.value.copy(isLoggedIn = false, user = null)
    }
}

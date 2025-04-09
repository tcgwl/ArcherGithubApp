package com.archer.github.app.ui.mine

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.archer.github.app.BuildConfig
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.local.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OAuthLoginViewModel @Inject constructor() : ViewModel() {

    @VisibleForTesting
    var repoProvider: () -> GithubRepository = { GithubRepository }

    fun requestToken(code: String, onFinish: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = repoProvider().oauthLogin(
                clientId = BuildConfig.CLIENT_ID,
                clientSecret = BuildConfig.CLIENT_SECRET,
                code = code
            )
            response.apply {
                onSuccess {
                    UserDao.saveAccessToken(it.access_token ?: "")
                    onFinish(false)
                }

                onFailure {
                    onFinish(true)
                }
            }
        }
    }
}

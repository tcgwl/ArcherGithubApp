package com.archer.github.app.ui.home

import androidx.annotation.VisibleForTesting
import com.archer.github.app.base.BaseViewModel
import com.archer.github.app.common.Constant
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.model.ErrorType
import com.archer.github.app.logic.model.SINCE_DROP_DOWN
import com.archer.github.app.logic.model.TrendRepoResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel() {

    private val _repoList = MutableStateFlow<List<TrendRepoResponse>?>(null)
    val repoList = _repoList.asStateFlow()

    private var selectedSince: String = SINCE_DROP_DOWN.options.first()
    private var selectedLanguage: String? = null

    @VisibleForTesting
    var repoProvider: () -> GithubRepository = { GithubRepository }

    fun getTrendRepoList(isRefresh: Boolean = true, since: String? = null, language: String? = null) {
        if (!isRefresh && _repoList.value != null) return

        since?.let { selectedSince = it }
        language?.let { selectedLanguage = it.takeIf { it != "All" } }

        launchWithErrorHandler {
            _loadingState.value = true

            val response = repoProvider().getTrendRepoList(true, Constant.API_TOKEN, selectedSince, selectedLanguage)
            response.apply {
                onSuccess {
                    _repoList.value = it
                }

                onFailure {
                    handleApiError(ErrorType.NETWORK_ERROR)
                }
            }

            _loadingState.value = false
        }
    }
}

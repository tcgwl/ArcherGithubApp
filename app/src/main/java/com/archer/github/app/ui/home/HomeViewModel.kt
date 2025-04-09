package com.archer.github.app.ui.home

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.archer.github.app.common.Constant
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.model.ErrorType
import com.archer.github.app.logic.model.SINCE_DROP_DOWN
import com.archer.github.app.logic.model.TrendRepoResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _loadingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _repoList: MutableStateFlow<List<TrendRepoResponse>?> = MutableStateFlow(null)
    val repoList = _repoList.asStateFlow()

    private val _errorState: MutableSharedFlow<ErrorType> = MutableSharedFlow()
    val errorState = _errorState

    private var selectedSince: String = SINCE_DROP_DOWN.options.first()
    private var selectedLanguage: String? = null

    @VisibleForTesting
    var repoProvider: () -> GithubRepository = { GithubRepository }

    fun getTrendRepoList(isRefresh: Boolean = true, since: String? = null, language: String? = null) {
        if (isRefresh.not() && _repoList.value != null) {
            return
        }

        since?.let { selectedSince = it }
        language?.let { selectedLanguage = it.takeIf { it != "All" } }

        val exception = CoroutineExceptionHandler { _, _ ->
            handleApiError(ErrorType.NETWORK_EXCEPTION)
        }

        viewModelScope.launch(exception) {
            _loadingState.value = true

            val response = repoProvider().getTrendRepoList(true, Constant.API_TOKEN, selectedSince, selectedLanguage)
            response.apply {
                onSuccess {
                    _repoList.value = it
                    _loadingState.value = false
                }
                onFailure {
                    handleApiError(ErrorType.NETWORK_ERROR)
                    _loadingState.value = false
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

package com.archer.github.app.ui.search

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.model.ErrorType
import com.archer.github.app.logic.model.Repository
import com.archer.github.app.logic.model.SearchResultResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val _loadingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _searchRepoResult: MutableStateFlow<SearchResultResponse<Repository>?> = MutableStateFlow(null)
    val searchRepoResult = _searchRepoResult.asStateFlow()

    private val _errorState: MutableSharedFlow<ErrorType> = MutableSharedFlow()
    val errorState = _errorState

    @VisibleForTesting
    var repoProvider: () -> GithubRepository = { GithubRepository }

    fun searchRepositories(query: String) {
        val exception = CoroutineExceptionHandler { _, _ ->
            handleApiError(ErrorType.NETWORK_EXCEPTION)
        }

        viewModelScope.launch(exception) {
            _loadingState.value = true
            val response = repoProvider().searchRepositories(query)
            response.apply {
                onSuccess {
                    _searchRepoResult.value = it
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

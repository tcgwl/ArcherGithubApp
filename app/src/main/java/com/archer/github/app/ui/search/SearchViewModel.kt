package com.archer.github.app.ui.search

import androidx.annotation.VisibleForTesting
import com.archer.github.app.base.BaseViewModel
import com.archer.github.app.common.Constant
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.model.ErrorType
import com.archer.github.app.logic.model.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : BaseViewModel() {

    private val _searchRepoResult = MutableStateFlow<List<Repository>>(emptyList())
    val searchRepoResult = _searchRepoResult.asStateFlow()

    private var totalCount = 0
    var currentPage = 1
        private set
    internal var isLastPage = false
    var isLoadingMore = false
        private set

    private var currentQuery = ""

    @VisibleForTesting
    var repoProvider: () -> GithubRepository = { GithubRepository }

    fun refresh(query: String) {
        currentPage = 1
        isLastPage = false
        currentQuery = query
        load(query, isRefresh = true)
    }

    fun loadMore() {
        if (isLoadingMore || isLastPage || currentQuery.isEmpty()) return
        isLoadingMore = true
        currentPage++
        load(currentQuery, isRefresh = false)
    }

    private fun load(query: String, isRefresh: Boolean) {
        launchWithErrorHandler {
            if (isRefresh) {
                _loadingState.value = true
            } else {
                isLoadingMore = true
            }

            val response = repoProvider().searchRepositories(query, currentPage)
            response.apply {
                onSuccess { result ->
                    val items = result.items
                    if (isRefresh) {
                        _searchRepoResult.value = items
                    } else {
                        _searchRepoResult.value += items
                    }

                    totalCount = result.totalCount?.toIntOrNull() ?: 0
                    isLastPage = if (totalCount > 0) {
                        _searchRepoResult.value.size >= totalCount
                    } else {
                        items.size < Constant.PAGE_SIZE
                    }
                }

                onFailure {
                    handleApiError(ErrorType.NETWORK_ERROR)
                    if (!isRefresh) currentPage-- // 回滚页码
                }
            }


            _loadingState.value = false
            isLoadingMore = false
        }
    }
}

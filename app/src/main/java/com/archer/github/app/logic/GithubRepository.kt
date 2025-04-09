package com.archer.github.app.logic

import com.archer.github.app.logic.model.AccessTokenResponse
import com.archer.github.app.logic.model.Repository
import com.archer.github.app.logic.model.SearchResultResponse
import com.archer.github.app.logic.model.TrendRepoResponse
import com.archer.github.app.logic.model.User
import com.archer.github.app.logic.network.ApiService
import com.archer.github.app.logic.network.RetrofitManager

/**
 * 仓库层的统一封装入口
 */
object GithubRepository {

    private val service by lazy { RetrofitManager.getService(ApiService::class.java) }

    suspend fun getTrendRepoList(
        forceNetWork: Boolean,
        apiToken: String,
        since: String,
        languageType: String?
    ): Result<List<TrendRepoResponse>> {
        return try {
            Result.success(
                service.getTrendRepoList(forceNetWork, apiToken, since, languageType)
            )
        } catch (exception: Throwable) {
            Result.failure(exception)
        }
    }

    suspend fun searchRepositories(
        query: String
    ): Result<SearchResultResponse<Repository>> {
        return try {
            Result.success(
                service.searchRepositories(query.plus("+language:Kotlin"))
            )
        } catch (exception: Throwable) {
            Result.failure(exception)
        }
    }

    suspend fun oauthLogin(
        clientId: String,
        clientSecret: String,
        code: String
    ): Result<AccessTokenResponse> {
        return try {
            Result.success(
                service.oauthLogin(clientId, clientSecret, code)
            )
        } catch (exception: Throwable) {
            Result.failure(exception)
        }
    }

    suspend fun getUserInfo(token: String): Result<User> {
        return try {
            Result.success(
                service.getUserInfo("Bearer $token")
            )
        } catch (exception: Throwable) {
            Result.failure(exception)
        }
    }

    suspend fun getUserRepoList(token: String): Result<List<Repository>> {
        return try {
            Result.success(
                service.getUserRepoList("Bearer $token")
            )
        } catch (exception: Throwable) {
            Result.failure(exception)
        }
    }
}

package com.archer.github.app.logic.network

import com.archer.github.app.common.Constant
import com.archer.github.app.logic.model.AccessTokenResponse
import com.archer.github.app.logic.model.Repository
import com.archer.github.app.logic.model.SearchResultResponse
import com.archer.github.app.logic.model.TrendRepoResponse
import com.archer.github.app.logic.model.User
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("https://guoshuyu.cn/github/trend/list")
    @Headers("Content-Type: text/plain;charset=utf-8")
    suspend fun getTrendRepoList(
        @Header("forceNetWork") forceNetWork: Boolean,
        @Header("api-token") apiToken: String,
        @Query("since") since: String,
        @Query("languageType") languageType: String?
    ): List<TrendRepoResponse>

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query(value = "q", encoded = true) query: String,
        @Query("sort") sort: String = "best%20match",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = Constant.PAGE_SIZE
    ): SearchResultResponse<Repository>

    @POST("https://github.com/login/oauth/access_token")
    @Headers("Accept: application/json")
    suspend fun oauthLogin(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("code") code: String
    ): AccessTokenResponse

    @GET("user")
    @Headers("Accept: application/json")
    suspend fun getUserInfo(
        @Header("Authorization") token: String
    ): User

    @GET("user/repos?visibility=all")
    suspend fun getUserRepoList(
        @Header("Authorization") token: String,
        @Query("sort") sort: String = "updated"
    ): List<Repository>
}

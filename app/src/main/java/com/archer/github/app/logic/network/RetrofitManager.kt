package com.archer.github.app.logic.network

import com.archer.github.app.BuildConfig
import com.archer.github.app.common.Constant
import com.archer.github.app.utils.LogUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit管理类
 */
object RetrofitManager {

    private val logInterceptor = HttpLoggingInterceptor { message ->
        LogUtil.d(message)
    }.setLevel(
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
    )

    private val client: OkHttpClient
        get() = OkHttpClient.Builder()
            .connectTimeout(Constant.TIME_OUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .build()

    fun <T> getService(serviceClass: Class<T>, baseUrl: String? = null): T {
        LogUtil.d(Constant.GITHUB_API_BASE_URL)
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl ?: Constant.GITHUB_API_BASE_URL)
            .build()
            .create(serviceClass)
    }
}

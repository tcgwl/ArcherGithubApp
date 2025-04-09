package com.archer.github.app.common

import androidx.core.net.toUri
import com.archer.github.app.BuildConfig

object Constant {

    const val GITHUB_BASE_URL = "https://github.com/"
    const val GITHUB_API_BASE_URL = "https://api.github.com/"
    const val API_TOKEN = "4d65e2a5626103f92a71867d7b49fea0"

    const val OAUTH_CALLBACK_URL = "githubapparcher://authed"
    val OAUTH_LOGIN_URL =
        "https://github.com/login/oauth/authorize?client_id=${BuildConfig.CLIENT_ID}&redirect_uri=$OAUTH_CALLBACK_URL".toUri()

    const val PAGE_SIZE = 30

    const val TIME_OUT_SECONDS = 8L

}

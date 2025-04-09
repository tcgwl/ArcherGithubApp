package com.archer.github.app.logic.local

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.core.content.edit
import com.archer.github.app.base.BaseApp

object UserDao {
    private const val FILE_NAME = "ArcherGithubApp"
    private const val KEY_ACCESS_TOKEN = "access_token"

    @VisibleForTesting
    var contextProvider: () -> Context = { BaseApp.appContext }

    fun saveAccessToken(token: String) {
        sharedPreferences().edit {
            putString(KEY_ACCESS_TOKEN, token)
        }
    }

    fun getAccessToken(): String? =
        sharedPreferences().getString(KEY_ACCESS_TOKEN, null)

    fun removeAccessToken() {
        sharedPreferences().edit {
            remove(KEY_ACCESS_TOKEN)
        }
    }

    private fun sharedPreferences() =
        contextProvider().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

}

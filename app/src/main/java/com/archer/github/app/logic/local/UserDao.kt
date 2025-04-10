package com.archer.github.app.logic.local

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit
import com.archer.github.app.base.BaseApp

/**
 * 使用 EncryptedSharedPreferences 安全存储 accessToken
 */
object UserDao {
    private const val FILE_NAME = "ArcherGithubApp_secure"
    private const val KEY_ACCESS_TOKEN = "access_token"

    @VisibleForTesting
    var contextProvider: () -> Context = { BaseApp.appContext }

    @VisibleForTesting
    var preferencesProvider: (() -> SharedPreferences)? = null

    private fun sharedPreferences(): SharedPreferences {
        return preferencesProvider?.invoke() ?: createEncryptedSharedPreferences()
    }

    private fun createEncryptedSharedPreferences(): SharedPreferences {
        val context = contextProvider()
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

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
}

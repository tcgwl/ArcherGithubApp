package com.archer.github.app

import com.archer.github.app.base.BaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GithubApp: BaseApp() {

    override fun onCreate() {
        super.onCreate()
        appViewModel = getAppViewModelProvider()[AppViewModel::class.java]
    }

    companion object {
        lateinit var appViewModel: AppViewModel
    }
}

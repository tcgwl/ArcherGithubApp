package com.archer.github.app.ui.web

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.archer.github.app.ui.widgets.TitleBar
import com.archer.github.app.utils.LocalNavHostController
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPage(url: String) {
    var pageTitle by remember { mutableStateOf("加载中...") }
    var progress by remember { mutableFloatStateOf(0.1f) }
    val webViewState = rememberWebViewState(url = url)
    val webViewNavigator = rememberWebViewNavigator()
    val navController = LocalNavHostController.current

    Column {
        TitleBar(title = pageTitle) {
            navController.popBackStack()
        }

        if (progress != 1.0f) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        WebView(state = webViewState,
            navigator = webViewNavigator,
            onCreated = {
                it.settings.run { javaScriptEnabled = true }
            },
            chromeClient = remember {
                object : AccompanistWebChromeClient() {
                    override fun onReceivedTitle(view: WebView, title: String?) {
                        super.onReceivedTitle(view, title)
                        pageTitle = title ?: ""
                    }

                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        progress = newProgress / 100.0f
                    }
                }
            })
    }
}

package com.archer.github.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import com.archer.github.app.common.Constant
import com.archer.github.app.ui.mine.OAuthLoginViewModel
import com.archer.github.app.ui.theme.ArcherGithubAppTheme
import com.archer.github.app.utils.ToastUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OAuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArcherGithubAppTheme {
                val viewModel: OAuthLoginViewModel = hiltViewModel()
                intent?.data?.let {
                    if (it.toString().startsWith(Constant.OAUTH_CALLBACK_URL)) {
                        val code = it.getQueryParameter("code")
                        if (code != null) {
                            viewModel.requestToken(code = code) { isFailed ->
                                if (isFailed) {
                                    ToastUtil.showShort(getString(R.string.login_error))
                                }
                                finish()
                            }
                        } else {
                            ToastUtil.showShort(getString(R.string.login_error))
                            finish()
                        }
                    }
                }
            }
        }

    }
}

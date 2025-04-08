package com.archer.github.app.utils

import android.widget.Toast
import com.archer.github.app.GithubApp

/**
 * Toast封装工具类
 */
object ToastUtil {

    fun showShort(msg: String) {
        Toast.makeText(GithubApp.context, msg, Toast.LENGTH_SHORT).show()
    }

    fun showLong(msg: String) {
        Toast.makeText(GithubApp.context, msg, Toast.LENGTH_LONG).show()
    }
}

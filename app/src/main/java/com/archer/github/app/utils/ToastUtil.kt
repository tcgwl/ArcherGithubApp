package com.archer.github.app.utils

import android.widget.Toast
import com.archer.github.app.base.BaseApp

/**
 * Toast封装工具类
 */
object ToastUtil {

    fun showShort(msg: String) {
        Toast.makeText(BaseApp.appContext, msg, Toast.LENGTH_SHORT).show()
    }

    fun showLong(msg: String) {
        Toast.makeText(BaseApp.appContext, msg, Toast.LENGTH_LONG).show()
    }
}

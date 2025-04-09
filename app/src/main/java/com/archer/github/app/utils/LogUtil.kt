package com.archer.github.app.utils

import android.util.Log
import com.archer.github.app.BuildConfig

/**
 * 日志打印工具类
 */
object LogUtil {

    /** 是否是调试状态，即是否打印日志 */
    private var isDebug = BuildConfig.DEBUG
    private var tag = "testHou"

    /**
     * 设置调试开关
     */
    fun isDebug(isDebug: Boolean) {
        this.isDebug = isDebug
    }

    /**
     * 设置打印日志的Tag
     */
    fun setTag(tag: String) {
        this.tag = tag
    }

    fun v(tag: String, msg: String) {
        if (isDebug) {
            Log.v(tag, msg)
        }
    }

    fun v(msg: String) {
        v(msg)
    }

    fun d(tag: String, msg: String) {
        if (isDebug) {
            Log.d(tag, msg)
        }
    }

    fun d(msg: String) {
        d(tag, msg)
    }

    fun i(tag: String, msg: String) {
        if (isDebug) {
            Log.i(tag, msg)
        }
    }

    fun i(msg: String) {
        i(tag, msg)
    }

    fun w(tag: String, msg: String) {
        if (isDebug) {
            Log.w(tag, msg)
        }
    }

    fun w(msg: String) {
        w(tag, msg)
    }

    fun e(tag: String, msg: String) {
        if (isDebug) {
            Log.e(tag, msg)
        }
    }

    fun e(msg: String) {
        e(tag, msg)
    }
}

package com.archer.github.app.logic.model

enum class ErrorType(val message: String) {
    NETWORK_ERROR("网络错误，请稍后再试"),
    NETWORK_EXCEPTION("网络异常，请稍后再试")
}

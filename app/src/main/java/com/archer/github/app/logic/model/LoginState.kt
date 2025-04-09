package com.archer.github.app.logic.model

data class LoginState(
    var isLoggedIn: Boolean = false,
    var user: User? = null
)

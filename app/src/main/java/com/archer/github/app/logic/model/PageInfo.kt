package com.archer.github.app.logic.model

data class PageInfo(
    var prev: Int = -1,
    var next: Int = -1,
    var last: Int = -1,
    var first: Int = -1
)

package com.archer.github.app.logic.model

data class TrendRepoResponse(
    var fullName: String = "",
    var url: String = "",
    var description: String = "",
    var language: String = "",
    var meta: String = "",
    var contributors: List<String> = arrayListOf(),
    var contributorsUrl: String = "",
    var starCount: String = "",
    var forkCount: String = "",
    var name: String = "",
    var reposName: String = ""
)

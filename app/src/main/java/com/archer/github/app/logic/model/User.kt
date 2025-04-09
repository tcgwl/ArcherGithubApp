package com.archer.github.app.logic.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class User(
    val login: String,
    val id: String,
    val name: String,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    val bio: String,
    val email: String,
    @SerializedName("public_repos")
    var publicRepos: Int = 0,
    @SerializedName("html_url")
    var htmlUrl: String? = null,
    var type: String? = null,
    var company: String? = null,
    var blog: String? = null,
    var location: String? = null,
    var starRepos: Int? = null,
    var honorRepos: Int? = null,
    @SerializedName("public_gists")
    var publicGists: Int = 0,
    var followers: Int = 0,
    var following: Int = 0,
    @SerializedName("created_at")
    var createdAt: Date? = null,
    @SerializedName("updated_at")
    var updatedAt: Date? = null,
)

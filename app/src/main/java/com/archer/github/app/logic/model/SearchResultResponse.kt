package com.archer.github.app.logic.model

import com.google.gson.annotations.SerializedName

import java.util.ArrayList

class SearchResultResponse<M> {
    @SerializedName("total_count")
    var totalCount: String? = null
    @SerializedName("incomplete_results")
    var incompleteResults: Boolean = false
    var items: ArrayList<M>? = null
}

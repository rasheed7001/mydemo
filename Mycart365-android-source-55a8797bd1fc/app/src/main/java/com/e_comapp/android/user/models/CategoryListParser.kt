package com.e_comapp.android.user.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CategoryListParser {
    @SerializedName("error")
    @Expose
    var error: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("categoryList")
    @Expose
    var categoryList: List<CategoryList>? = null

    inner class CategoryList {
        @SerializedName("Category")
        @Expose
        var category: String? = null

    }
}
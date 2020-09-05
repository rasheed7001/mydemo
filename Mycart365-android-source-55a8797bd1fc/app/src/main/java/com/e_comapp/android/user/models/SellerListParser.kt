package com.e_comapp.android.user.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SellerListParser : Serializable {
    @SerializedName("error")
    @Expose
    var error: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("sellerList")
    @Expose
    var sellerList: List<SellerList>? = null

    inner class SellerList : Serializable {
        @SerializedName("Id")
        @Expose
        var id: String? = null

        @SerializedName("FirstName")
        @Expose
        var firstName: String? = null

        @SerializedName("LastName")
        @Expose
        var lastName: String? = null

        @SerializedName("Email")
        @Expose
        var email: String? = null

        @SerializedName("CompanyName")
        @Expose
        var companyName: String? = null

        @SerializedName("Address_1")
        @Expose
        var address1: String? = null

        @SerializedName("Pincode")
        @Expose
        var pincode: String? = null

        @SerializedName("Industry")
        @Expose
        var industry: String? = null

        @SerializedName("Logo")
        @Expose
        var logo: String? = null

    }
}
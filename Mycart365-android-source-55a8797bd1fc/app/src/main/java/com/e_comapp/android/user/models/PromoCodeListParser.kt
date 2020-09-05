package com.e_comapp.android.user.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PromoCodeListParser : Serializable {
    @SerializedName("error")
    @Expose
    var error: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("promoCodeList")
    @Expose
    var promoCodeList: List<PromoCodeList>? = null

    inner class PromoCodeList : Serializable {
        @SerializedName("Id")
        @Expose
        var id: String? = null

        @SerializedName("SellerId")
        @Expose
        var sellerId: String? = null

        @SerializedName("PromoCode")
        @Expose
        var promoCode: String? = null

        @SerializedName("PromoDesc")
        @Expose
        var promoDesc: String? = null

        @SerializedName("PromoCodeType")
        @Expose
        var promoCodeType: String? = null

        @SerializedName("PromoCodeValue")
        @Expose
        var promoCodeValue: String? = null

        @SerializedName("ExpiryDate")
        @Expose
        var expiryDate: String? = null

        @SerializedName("Status")
        @Expose
        var status: String? = null

    }
}
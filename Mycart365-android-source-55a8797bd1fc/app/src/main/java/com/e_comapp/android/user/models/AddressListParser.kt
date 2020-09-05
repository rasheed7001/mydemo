package com.e_comapp.android.user.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddressListParser {
    @SerializedName("error")
    @Expose
    var error: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("deliveryAddressList")
    @Expose
    var deliveryAddressList: List<DeliveryAddressList>? = null

    inner class DeliveryAddressList {
        @SerializedName("Id")
        @Expose
        var id: String? = null

        @SerializedName("fullAddress")
        @Expose
        var fullAddress: String? = null

        @SerializedName("address1")
        @Expose
        var address1: String? = null

        @SerializedName("address2")
        @Expose
        var address2: String? = null

        @SerializedName("landMark")
        @Expose
        var landMark: String? = null

        @SerializedName("others")
        @Expose
        var others: String? = null

        @SerializedName("contactPerson")
        @Expose
        var contactPerson: String? = null

        @SerializedName("addressType")
        @Expose
        var addressType: String? = null

        @SerializedName("userId")
        @Expose
        var userId: String? = null

        @SerializedName("Timestamp")
        @Expose
        var timestamp: String? = null
        var isSelected = false

    }
}
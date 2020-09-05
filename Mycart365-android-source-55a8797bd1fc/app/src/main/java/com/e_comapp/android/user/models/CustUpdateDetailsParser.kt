package com.e_comapp.android.user.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CustUpdateDetailsParser {
    @SerializedName("error")
    @Expose
    var error: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("userDetails")
    @Expose
    var userDetails: UserDetails? = null

    inner class UserDetails {
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

        @SerializedName("AccessToken")
        @Expose
        var accessToken: String? = null

        @SerializedName("Status")
        @Expose
        var status: String? = null

        @SerializedName("Otp_Status")
        @Expose
        var otpStatus: String? = null

    }
}
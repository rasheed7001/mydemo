package com.e_comapp.android.seller.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SellerRegParser(@SerializedName("error")
                           @Expose
                           private var error: Boolean? = null,

                           @SerializedName("userType")
                           @Expose
                           private var userType: String? = null,

                           @SerializedName("message")
                           @Expose
                           private var message: String? = null,

                           @SerializedName("userDetails")
                           @Expose
                           var userDetails: UserDetails? = null,

                           @SerializedName("otp")
                           @Expose
                           var otp: Int? = null)

data class UserDetails(@SerializedName("Id")
                       @Expose
                       var id: String? = null,

                       @SerializedName("CompanyName")
                       @Expose
                       private var companyName: String? = null,

                       @SerializedName("Gst_No")
                       @Expose
                       private var gstNo: String? = null,

                       @SerializedName("Address_1")
                       @Expose
                       private var address1: String? = null,

                       @SerializedName("Pincode")
                       @Expose
                       private var pincode: String? = null,

                       @SerializedName("FirstName")
                       @Expose
                       private var firstName: String? = null,

                       @SerializedName("LastName")
                       @Expose
                       private var lastName: String? = null,

                       @SerializedName("Email")
                       @Expose
                       private var email: String? = null,

                       @SerializedName("AccessToken")
                       @Expose
                       var accessToken: String? = null,

                       @SerializedName("Status")
                       @Expose
                       var status: String? = null,

                       @SerializedName("Otp_Status")
                       @Expose
                       var otpStatus: String? = null)

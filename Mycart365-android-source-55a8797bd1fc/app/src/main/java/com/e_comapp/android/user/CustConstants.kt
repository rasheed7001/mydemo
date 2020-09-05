package com.e_comapp.android.user

import com.e_comapp.android.user.models.ProductListParser
import java.util.*

object CustConstants {
    var selectedProdList = ArrayList<ProductListParser.ProductList?>()
    var deliveryType = 1
    var subTotal = 0.0
    var deliveryCharge = 0.0
    var totalAmt = 0.0
    var isFromCheckOut = false
    var sellerId = ""
    var custId = ""
}
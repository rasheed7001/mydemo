package com.e_comapp.android.user.listeners

import com.e_comapp.android.user.models.SellerListParser.SellerList

interface OnSellerItemClickedListener {
    fun onSellerItemClicked(sellerList: SellerList?)
}
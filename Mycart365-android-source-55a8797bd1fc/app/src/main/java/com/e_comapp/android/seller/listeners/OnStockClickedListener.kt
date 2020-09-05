package com.e_comapp.android.seller.listeners

interface OnStockClickedListener {
    fun onUpdateClicked()
    fun onDeleteClicked(productId: Int)
}
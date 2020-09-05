package com.e_comapp.android.user.models

import android.widget.ArrayAdapter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ProductListParser(var product: ProductList?) {
    @SerializedName("error")
    @Expose
    var error: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("productList")
    @Expose
    var productList: List<ProductList>? = null

    inner class ProductList {
        @SerializedName("Id")
        @Expose
        var id: String? = null

        @SerializedName("ProductName")
        @Expose
        var productName: String? = null

        @SerializedName("Category")
        @Expose
        var category: String? = null

        @SerializedName("Brand")
        @Expose
        var brand: String? = null

        @SerializedName("ProductDetails")
        @Expose
        var productDetails: String? = null

        @SerializedName("ExpiryDate")
        @Expose
        var expiryDate: String? = null

        @SerializedName("ProdImg")
        @Expose
        var prodImg: String? = null

        @SerializedName("ProductStatus")
        @Expose
        var productStatus: String? = null

        @SerializedName("Created")
        @Expose
        var created: String? = null

        @SerializedName("Timestamp")
        @Expose
        var timestamp: String? = null

        @SerializedName("SellerId")
        @Expose
        var sellerId: String? = null

        @SerializedName("IngredientType")
        @Expose
        var ingredientType: String? = null

        @SerializedName("unitDetails")
        @Expose
        var unitDetails: List<UnitDetail?>? = null

        @SerializedName("prodImages")
        @Expose
        var prodImages: List<ProdImage?>? = null

        var qty = 0
        var selectedUnitPos = 0
        var seletecdUnitId = ""
        var seletecdUnitType = ""
        var dataAdapter: ArrayAdapter<UnitDetail>? = null

        inner class ProdImage {
            @SerializedName("Id")
            @Expose
            var id: String? = null

            @SerializedName("ImgUrl")
            @Expose
            var imgUrl: String? = null
        }

        inner class UnitDetail {
            @SerializedName("Id")
            @Expose
            var id: String? = null

            @SerializedName("ProdId")
            @Expose
            var prodId: String? = null

            @SerializedName("UnitType")
            @Expose
            var unitType: String? = null

            @SerializedName("UnitPrice")
            @Expose
            var unitPrice: String? = null

            @SerializedName("OfferPrice")
            @Expose
            var offerPrice: String? = null

            @SerializedName("Status")
            @Expose
            var status: String? = null

            @SerializedName("NetStock")
            @Expose
            var netStock: String? = null

            @SerializedName("AvailStock")
            @Expose
            var availStock: String? = null

        }
    }

}
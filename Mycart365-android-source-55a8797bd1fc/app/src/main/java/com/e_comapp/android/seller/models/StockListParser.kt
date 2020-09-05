package com.e_comapp.android.seller.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StockListParser(@SerializedName("error")
                      @Expose
                      var error: Boolean? = null,

                      @SerializedName("message")
                      @Expose
                      private var message: String? = null,

                      @SerializedName("productList")
                      @Expose
                      var productList: List<ProductList>? = null)


data class ProductList(@SerializedName("Id")
                       @Expose
                       var id: String? = null,

                       @SerializedName("ProductName")
                       @Expose
                       var productName: String? = null,

                       @SerializedName("Category")
                       @Expose
                       var category: String? = null,

                       @SerializedName("Brand")
                       @Expose
                       var brand: String? = null,

                       @SerializedName("ProductDetails")
                       @Expose
                       var productDetails: String? = null,

                       @SerializedName("ExpiryDate")
                       @Expose
                       var expiryDate: String? = null,

                       @SerializedName("ProdImg")
                       @Expose
                       var prodImg: String? = null,

                       @SerializedName("ProductStatus")
                       @Expose
                       var productStatus: String? = null,

                       @SerializedName("Created")
                       @Expose
                       var created: String? = null,

                       @SerializedName("Timestamp")
                       @Expose
                       var timestamp: String? = null,

                       @SerializedName("SellerId")
                       @Expose
                       var sellerId: String? = null,

                       @SerializedName("IngredientType")
                       @Expose
                       var ingredientType: String? = null,

                       @SerializedName("unitDetails")
                       @Expose
                       var unitDetails: List<UnitDetail?>? = null)

data class UnitDetail(@SerializedName("Id")
                      @Expose
                      var id: String? = null,

                      @SerializedName("ProdId")
                      @Expose
                      var prodId: String? = null,

                      @SerializedName("UnitType")
                      @Expose
                      var unitType: String? = null,

                      @SerializedName("UnitPrice")
                      @Expose
                      var unitPrice: String? = null,

                      @SerializedName("OfferPrice")
                      @Expose
                      var offerPrice: String? = null,

                      @SerializedName("Status")
                      @Expose
                      var status: String? = null,

                      @SerializedName("NetStock")
                      @Expose
                      var netStock: String? = null,

                      @SerializedName("AvailStock")
                      @Expose
                      var availStock: String? = null)

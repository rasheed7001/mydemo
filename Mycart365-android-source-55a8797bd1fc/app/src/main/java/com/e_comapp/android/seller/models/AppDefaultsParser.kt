package com.e_comapp.android.seller.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AppDefaultsParser(@SerializedName("error")
                        @Expose
                        private var error: Boolean? = null,

                        @SerializedName("industry")
                        @Expose
                        var industry: List<Industry>? = null,

                        @SerializedName("category")
                        @Expose
                        private var category: List<Category>? = null,

                        @SerializedName("states")
                        @Expose
                        var states: List<State>? = null,

                        @SerializedName("cities")
                        @Expose
                        var cities: List<City>? = null)


data class Category(
        @SerializedName("Id")
        @Expose
        private var id: String? = null,

        @SerializedName("Category")
        @Expose
        private var category: String? = null,

        @SerializedName("Status")
        @Expose
        private var status: String? = null)

data class City(
        @SerializedName("id")
        @Expose
        var id: String? = null,

        @SerializedName("city")
        @Expose
        var city: String? = null,

        @SerializedName("state_id")
        @Expose
        var stateId: String? = null)

data class Industry(@SerializedName("Id")
                    @Expose
                    var id: String? = null,

                    @SerializedName("Industry")
                    @Expose
                    public var industry: String? = null,

                    @SerializedName("ColorCode")
                    @Expose
                    var colorCode: String? = null,

                    @SerializedName("Img")
                    @Expose
                     var img: String? = null,

                    @SerializedName("Status")
                    @Expose
                     var status: String? = null,

                    @SerializedName("ImgUrl")
                    @Expose
                    var imgUrl: String? = null)

data class State(
        @SerializedName("id")
        @Expose
        var id: String? = null,

        @SerializedName("name")
        @Expose
        var name: String? = null,

        @SerializedName("country_id")
        @Expose
        var countryId: String? = null)

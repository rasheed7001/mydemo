package com.e_comapp.android.viewmodel

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.e_comapp.android.EComApp.Companion.app
import com.e_comapp.android.liadatautil.toSingleEvent
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.CustConstants
import com.e_comapp.android.user.activities.SetDeliveryLocationActivity
import com.e_comapp.android.user.models.CategoryListParser
import com.e_comapp.android.user.models.OrderedProdModel
import com.e_comapp.android.user.models.SellerListParser
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.utils.TextUtils
import com.e_comapp.android.utils.constructJson
import com.e_comapp.android.viewmodel.base.CoroutinesViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import cz.msebera.android.httpclient.entity.StringEntity
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*
import kotlin.collections.ArrayList

class ProductListViewModel : CoroutinesViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading.toSingleEvent()

    private val _apiError = MutableLiveData<String>()
    val apiError: LiveData<String> = _apiError.toSingleEvent()

    private val _categoryListResponse = MutableLiveData<List<CategoryListParser.CategoryList>>()
    val categoryListResponse: LiveData<List<CategoryListParser.CategoryList>> = _categoryListResponse.toSingleEvent()

    private val _checkQtySuccessResponse = MutableLiveData<String>()
    val checkQtySuccessResponse: LiveData<String> = _checkQtySuccessResponse.toSingleEvent()

    val listItem = MediatorLiveData<List<CategoryListParser.CategoryList>>().apply {
        addSource(_categoryListResponse) {
            value = it
        }
    }

    fun callGetCustCategoryList(sellerId: String) {
        _isLoading.value = true
        val map = HashMap<String, String>()
        map["start"] = "0"
        map["sellerId"] = sellerId
        app?.retrofitInterface?.getCategoryListSeller("C", constructJson(map))?.enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                _isLoading.value = false
                val parser = Gson().fromJson(content, CategoryListParser::class.java)
                _categoryListResponse.value = parser.categoryList as ArrayList<CategoryListParser.CategoryList>
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                _isLoading.value = false
            }
        })
    }


    fun callCheckQtyApi() {

        _isLoading.value = true
        app?.retrofitInterface?.postCheckProductQty("C", params)?.enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                _isLoading.value = false
                _checkQtySuccessResponse.value = content
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                _isLoading.value = false
            }
        })
    }

    private val params: JsonObject
        private get() {
            val entity: StringEntity? = null
            var `object` = JsonObject()
            try {
                val orderedProdList: MutableList<OrderedProdModel> = java.util.ArrayList()
                for (productList in CustConstants.selectedProdList) {
                    val model = OrderedProdModel()
                    model.prodId = productList?.id
                    model.unitId = productList!!.seletecdUnitId
                    model.Qty = "" + productList.qty
                    for (unitDetail in productList.unitDetails!!) {
                        if (unitDetail?.id.equals(productList.seletecdUnitId, ignoreCase = true)) {
                            if (TextUtils.isNullOrEmpty(unitDetail?.offerPrice) ||
                                    unitDetail?.offerPrice.equals("0.00", ignoreCase = true)) {
                                val totalAmt = unitDetail?.unitPrice?.toDouble()!! * productList.qty
                                model.TotalAmt = "" + totalAmt
                                model.UnitPrice = "" + unitDetail.unitPrice
                            } else {
                                val totalAmt = unitDetail?.offerPrice?.toDouble()!! * productList.qty
                                model.TotalAmt = "" + totalAmt
                                model.UnitPrice = "" + unitDetail.offerPrice
                            }
                        }
                    }
                    orderedProdList.add(model)
                }
                val prodArray = Gson().toJson(orderedProdList)
                val json = "{\"productList\":" + prodArray +
                        "}"
                `object` = JsonParser().parse(json).asJsonObject
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.e("Product List View Model", `object`.toString())
            return `object`
        }


    /*fun searchListItem(query: String) {
        val list = _sellerListResponse.value?.toMutableList()

        val searchItem = emptyList<SellerListParser.SellerList>().toMutableList()

        if (TextUtils.isEmpty(query)) {
            if (list != null) {
                searchItem.addAll(list)
            }
        } else {
            list?.forEach {
                if (!TextUtils.isEmpty(it.companyName) && it.companyName!!.contains(query,true)) {
                    searchItem.add(it)
                }
            }
        }

        listItem.value = searchItem
    }*/
}
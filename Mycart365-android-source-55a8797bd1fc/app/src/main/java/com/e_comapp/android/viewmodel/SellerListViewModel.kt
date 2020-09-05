package com.e_comapp.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.e_comapp.android.EComApp.Companion.app
import com.e_comapp.android.liadatautil.toSingleEvent
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.models.SellerListParser
import com.e_comapp.android.utils.TextUtils
import com.e_comapp.android.utils.constructJson
import com.e_comapp.android.viewmodel.base.CoroutinesViewModel
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*
import kotlin.collections.ArrayList

class SellerListViewModel : CoroutinesViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading.toSingleEvent()

    private val _apiError = MutableLiveData<String>()
    val apiError: LiveData<String> = _apiError.toSingleEvent()

    private val _sellerListResponse = MutableLiveData<List<SellerListParser.SellerList>>()
    val sellerListResponse: LiveData<List<SellerListParser.SellerList>> = _sellerListResponse.toSingleEvent()

    val listItem = MediatorLiveData<List<SellerListParser.SellerList>>().apply {
        addSource(_sellerListResponse){
            value = it
        }
    }

    fun callGetCustSellers(industry: String) {
        _isLoading.value = true
        val map = HashMap<String, String>()
        map["start"] = "0"
        map["industry"] = industry
        app?.retrofitInterface?.postGetCustSellers("C", constructJson(map))?.enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)

                _isLoading.value = false
                val parser = Gson().fromJson(content, SellerListParser::class.java)
                if (!parser.error!!) {
                    if (!parser.sellerList?.isEmpty()!!) {
                        _sellerListResponse.value = parser.sellerList as ArrayList<SellerListParser.SellerList>
                    }
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                _isLoading.value = false

                _apiError.value = message
            }
        })
    }


    fun searchListItem(query: String) {
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
    }
}
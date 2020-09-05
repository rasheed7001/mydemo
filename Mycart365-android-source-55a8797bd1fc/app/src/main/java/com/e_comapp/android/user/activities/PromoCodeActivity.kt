package com.e_comapp.android.user.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.activities.PromoCodeActivity
import com.e_comapp.android.user.adapters.PromoCodeAdapter
import com.e_comapp.android.user.adapters.PromoCodeAdapter.OnPromoCodeClickedListener
import com.e_comapp.android.user.models.PromoCodeListParser
import com.e_comapp.android.user.models.PromoCodeListParser.PromoCodeList
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*

class PromoCodeActivity : BaseActivity(), OnPromoCodeClickedListener {
    var btnApply: CustomBtn? = null
    var etCode: CustomEditText? = null
    var recyclerView: RecyclerView? = null
    var adapter: PromoCodeAdapter? = null
    var promoCodeList: ArrayList<PromoCodeList?>? = null
    private val TAG = javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promo_code)
        init()
        setupDefaults()
        setupEvents()
    }

    override fun menuClicked() {
        super.menuClicked()
        onBackPressed()
    }

    private fun init() {
        btnApply = findViewById(R.id.btnApply)
        etCode = findViewById(R.id.etPromoCode)
        recyclerView = findViewById(R.id.rvPromoCodeList)
        promoCodeList = ArrayList()
        adapter = PromoCodeAdapter(promoCodeList, this, this)
    }

    private fun setupDefaults() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        setCustTitle(getString(R.string.str_promocode))
        recyclerView!!.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView!!.adapter = adapter
        callGetPromoCode()
    }

    private fun setupEvents() {}
    private fun constructJson(): JsonObject {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("sellerId", app.appPreference?.getSellerId())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    fun callGetPromoCode() {
        DeviceUtils.hideSoftKeyboard(this)
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.getPromoCode("C", constructJson()).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                Log.e(TAG, content)
                dialog!!.hide()
                if (content != null && !content.isEmpty()) {
                    val parser = Gson().fromJson(content, PromoCodeListParser::class.java)
                    if (!parser.error!!) {
                        if (!parser.promoCodeList?.isEmpty()!!) {
                            promoCodeList = parser.promoCodeList as ArrayList<PromoCodeList?>
                            adapter!!.updateList(promoCodeList)
                        }
                    }
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(this@PromoCodeActivity, message)
            }
        })
    }

    override fun onPromoCodeClicked(promoCode: PromoCodeList?) {
        val code = promoCode?.promoCode
        val intent = Intent()
        intent.putExtra("promo_code", promoCode)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
package com.e_comapp.android.user.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.CustConstants
import com.e_comapp.android.user.activities.MyCartActivity
import com.e_comapp.android.user.adapters.MyCartRecyclerAdapter
import com.e_comapp.android.user.models.OrderedProdModel
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.AppConstants
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.utils.TextUtils
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomTextView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import cz.msebera.android.httpclient.entity.StringEntity
import okhttp3.ResponseBody
import retrofit2.Call
import java.util.*

class MyCartActivity : BaseActivity() {
    var adapter: MyCartRecyclerAdapter? = null
    var recyclerView: RecyclerView? = null
    var btnCheckOut: CustomBtn? = null
    var btnAddProduct: CustomBtn? = null
    var textItemCount: CustomTextView? = null
    var textTotal: CustomTextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_my_cart)
        init()
        setupDefaults()
        setupEvents()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.Companion.RC_PRODUCT_LIST_USER_ACTIVITY) {
                adapter!!.updateList(CustConstants.selectedProdList)
                textTotal!!.text = CustConstants.subTotal.toString() + ""
            }
        }
    }

    override fun menuClicked() {
        super.menuClicked()
        onBackPressed()
    }

    private fun init() {
        textItemCount = findViewById(R.id.textItemCount)
        textTotal = findViewById(R.id.textTotal)
        recyclerView = findViewById(R.id.rv_items)
        btnCheckOut = findViewById(R.id.btnCheckout)
        btnAddProduct = findViewById(R.id.btnAddProduct)
        adapter = MyCartRecyclerAdapter(CustConstants.selectedProdList, this)
    }

    private fun setupDefaults() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        setCustTitle(getString(R.string.str_my_cart))
//        adapter!!.setActivity(this)
        textItemCount!!.text = CustConstants.selectedProdList.size.toString() + " " + "Items"
        textTotal!!.text = CustConstants.subTotal.toString() + ""
        recyclerView!!.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView!!.adapter = adapter
    }

    private fun setupEvents() {
        btnAddProduct!!.setOnClickListener {
            val intent = Intent(this@MyCartActivity, ProductListUserActivity::class.java)
            intent.putExtra(AppConstants.Companion.FROM_CONFIRM_ORDER, true)
            startActivityForResult(intent, AppConstants.Companion.RC_PRODUCT_LIST_USER_ACTIVITY)
        }
        btnCheckOut!!.setOnClickListener { callCheckQtyApi() }
    }

    private val params: JsonObject
        private get() {
            val entity: StringEntity? = null
            var `object` = JsonObject()
            try {
                val orderedProdList: MutableList<OrderedProdModel> = ArrayList()
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
            return `object`
        }

    private fun callCheckQtyApi() {
        DeviceUtils.hideSoftKeyboard(this)
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.postCheckProductQty("C", params).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.dismiss()
                val intent = Intent(this@MyCartActivity, SetDeliveryLocationActivity::class.java)
                startActivity(intent)
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.dismiss()
            }
        })
    }

    fun calculateAmt() {
        var totalAmt = 0.0
        for (item in CustConstants.selectedProdList) {
            var qtyAmt = 0.0
            for (unitDetail in item?.unitDetails!!) {
                if (unitDetail?.id.equals(item.seletecdUnitId, ignoreCase = true)) {
                    qtyAmt = if (TextUtils.isNullOrEmpty(unitDetail?.offerPrice) ||
                            unitDetail?.offerPrice.equals("0.00", ignoreCase = true)) {
                        unitDetail?.unitPrice?.toDouble()!! * item.qty
                    } else {
                        unitDetail?.offerPrice?.toDouble()!! * item.qty
                    }
                }
                break
            }
            totalAmt = totalAmt + qtyAmt
        }
        textTotal!!.text = totalAmt.toString() + ""
        CustConstants.subTotal = totalAmt
    }
}
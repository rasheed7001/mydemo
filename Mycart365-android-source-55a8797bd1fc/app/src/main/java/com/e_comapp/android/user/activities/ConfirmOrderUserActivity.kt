package com.e_comapp.android.user.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.CustConstants
import com.e_comapp.android.user.DeliveryAddressDialog
import com.e_comapp.android.user.DeliveryTypeDialog
import com.e_comapp.android.user.activities.ConfirmOrderUserActivity
import com.e_comapp.android.user.adapters.DeliveryAddressAdapter
import com.e_comapp.android.user.adapters.DeliveryAddressAdapter.DeliveryAddressClickListener
import com.e_comapp.android.user.adapters.DeliveryTypeAdapter
import com.e_comapp.android.user.adapters.DeliveryTypeAdapter.DeliveryTypeClickListener
import com.e_comapp.android.user.adapters.MyCartRecyclerAdapter
import com.e_comapp.android.user.models.AddressListParser
import com.e_comapp.android.user.models.AddressListParser.DeliveryAddressList
import com.e_comapp.android.user.models.DeliveryTypeModel
import com.e_comapp.android.user.models.OrderedProdModel
import com.e_comapp.android.user.models.PromoCodeListParser.PromoCodeList
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.AppConstants
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.utils.TextUtils
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomEditText
import com.e_comapp.android.views.CustomTextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import cz.msebera.android.httpclient.entity.StringEntity
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.util.*

class ConfirmOrderUserActivity : BaseActivity(), DeliveryTypeClickListener, DeliveryAddressClickListener {
    val TAG = javaClass.simpleName
    var adapter: MyCartRecyclerAdapter? = null
    var recyclerView: RecyclerView? = null
    var btnPay: CustomBtn? = null
    var btnAddProduct: CustomBtn? = null
    var expandDeliveryType: ImageView? = null
    var expandAddress: ImageView? = null
    var rlPromocode: RelativeLayout? = null
    var textItemCount: CustomTextView? = null
    var textDeliveryTypeDesc: CustomTextView? = null
    var textDeliveryType: CustomTextView? = null
    var textAddress: CustomTextView? = null
    var textSubTotal: CustomTextView? = null
    var textDeliveryCharge: CustomTextView? = null
    var textTotal: CustomTextView? = null
    var etPromoCode: CustomEditText? = null
    var customDialog: Dialog? = null
    var addressDialog: DeliveryAddressDialog? = null
    var promoCode: PromoCodeList? = null
    var promoCodeId: String? = ""
    var deliveryTypeList: ArrayList<DeliveryTypeModel>? = null
    var addressList: ArrayList<DeliveryAddressList?>? = null
    var localId = "1"
    var deliveryType: String? = "1"
    var deliveryTypeTime = "1"
    var custId: String? = null
    var sellerId = CustConstants.sellerId
    var totalItems = "" + CustConstants.selectedProdList.size
    var deliveryAddressId: String? = null
    var deliveryCharge = 30.0
    var subTotal = CustConstants.subTotal
    var totalAmt = 0.0
    var choosenAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order_user)
        init()
        setupDefaults()
        setupEvents()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.Companion.RC_PRODUCT_LIST_USER_ACTIVITY) {
                adapter!!.updateList(CustConstants.selectedProdList)
                textSubTotal!!.text = CustConstants.subTotal.toString() + ""
                subTotal = CustConstants.subTotal
                totalAmt = CustConstants.subTotal + deliveryCharge
                textTotal!!.text = totalAmt.toString() + ""
                CustConstants.totalAmt = totalAmt
            }
            if (requestCode == AppConstants.Companion.RC_PROMO_CODE_ACTIVITY) {
                if (data != null && data.hasExtra("promo_code")) {
                    promoCode = data.getSerializableExtra("promo_code") as PromoCodeList
                    etPromoCode?.setText(promoCode?.promoCode)
                    promoCodeId = promoCode?.id
                }
            }
        }
    }

    override fun menuClicked() {
        super.menuClicked()
        onBackPressed()
    }

    override fun rightMenuClicked() {
        super.rightMenuClicked()
        val intent = Intent(this@ConfirmOrderUserActivity, MyCartActivity::class.java)
        startActivity(intent)
    }

    private fun init() {
        custId = app.appPreference?.getAppUserId()
        deliveryTypeList = ArrayList()
        addressList = ArrayList()
        expandDeliveryType = findViewById(R.id.imageExpand1)
        expandAddress = findViewById(R.id.imageExpand2)
        textDeliveryTypeDesc = findViewById(R.id.textDeliveryDescription)
        textDeliveryType = findViewById(R.id.textStandardDelivery)
        textAddress = findViewById(R.id.textAddress)
        textSubTotal = findViewById(R.id.textSubTotalPrice)
        textDeliveryCharge = findViewById(R.id.textDelieryChargePrice)
        textTotal = findViewById(R.id.txtTotal)
        textItemCount = findViewById(R.id.textItemCount)
        rlPromocode = findViewById(R.id.rl_promocode)
        etPromoCode = findViewById(R.id.etPromoCode)
        recyclerView = findViewById(R.id.rv_items)
        adapter = MyCartRecyclerAdapter(CustConstants.selectedProdList, this)
        btnPay = findViewById(R.id.btnCheckout)
        btnAddProduct = findViewById(R.id.btnAddProduct)
    }

    private fun setupDefaults() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        setRightMenuIcon(R.drawable.my_cart_white)
        setCustTitle(getString(R.string.str_confirm_order))
        callAddressListApi()
        textItemCount!!.text = CustConstants.selectedProdList.size.toString() + " " + "Items"
        deliveryTypeList!!.add(DeliveryTypeModel("Standad Delivery", 30.0, "7 am - 2pm", true))
        deliveryTypeList!!.add(DeliveryTypeModel("Immediate Delivery", 40.0, "7 am - 12pm", false))
        recyclerView!!.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView!!.adapter = adapter
        textDeliveryTypeDesc?.setText(deliveryTypeList!![0].deliveryType + " - " + deliveryTypeList!![0].deliveryTypeTime)
        textDeliveryType?.setText(deliveryTypeList!![0].deliveryType)
        deliveryCharge = deliveryTypeList!![0].deliveryCharge
        deliveryType = deliveryTypeList!![0].deliveryType
        totalAmt = subTotal + deliveryCharge
        textSubTotal!!.text = subTotal.toString() + ""
        textDeliveryCharge!!.text = deliveryCharge.toString() + ""
        textTotal!!.text = totalAmt.toString() + ""
        CustConstants.totalAmt = totalAmt
    }

    private fun setupEvents() {
        expandDeliveryType!!.setOnClickListener {
            val dataAdapter = DeliveryTypeAdapter(deliveryTypeList, this@ConfirmOrderUserActivity)
/*            customDialog = DeliveryTypeDialog(this@ConfirmOrderUserActivity, )
            customDialog?.show()
            customDialog?.setCanceledOnTouchOutside(false)*/

            val bottomSheetFragment = DeliveryTypeDialog(this,dataAdapter, choosenAddress);
            bottomSheetFragment.show(supportFragmentManager, "dialog")
        }

        expandAddress!!.setOnClickListener {
            val dataAdapter = DeliveryAddressAdapter(this@ConfirmOrderUserActivity, addressList, this@ConfirmOrderUserActivity)
            /*customDialog = DeliveryAddressDialog(this@ConfirmOrderUserActivity, dataAdapter, choosenAddress)
            customDialog?.show()
            customDialog?.setCanceledOnTouchOutside(false)*/

            addressDialog = DeliveryAddressDialog(dataAdapter, choosenAddress)
            addressDialog?.show(supportFragmentManager, "Address dialog")
        }
        btnAddProduct!!.setOnClickListener {
            val intent = Intent(this@ConfirmOrderUserActivity, ProductListUserActivity::class.java)
            intent.putExtra(AppConstants.Companion.FROM_CONFIRM_ORDER, true)
            startActivityForResult(intent, AppConstants.Companion.RC_PRODUCT_LIST_USER_ACTIVITY)
        }
        rlPromocode!!.setOnClickListener {
            val intent = Intent(this@ConfirmOrderUserActivity, PromoCodeActivity::class.java)
            startActivityForResult(intent, AppConstants.Companion.RC_PROMO_CODE_ACTIVITY)
        }
        btnPay!!.setOnClickListener { callCheckQtyApi() }
    }

    override fun clickOnItem(data: DeliveryTypeModel, position: Int) {
        if (customDialog != null) {
            customDialog!!.dismiss()
            for (model in deliveryTypeList!!) {
                model.isSelected = model.deliveryType == data.deliveryType
            }
            textDeliveryType?.setText(data.deliveryType)
            textDeliveryTypeDesc?.setText(data.deliveryType + " - " + data.deliveryTypeTime)
            deliveryCharge = data.deliveryCharge
            totalAmt = CustConstants.subTotal + deliveryCharge
            textDeliveryCharge!!.text = deliveryCharge.toString() + ""
            textTotal!!.text = totalAmt.toString() + ""
            CustConstants.totalAmt = totalAmt
            deliveryType = if (data.deliveryType == "Standad Delivery") {
                "1"
            } else {
                "2"
            }
        }
    }

    private fun constructJson(start: Int): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("start", start)
        return jsonObject
    }

    private fun callAddressListApi() {
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.getDeliveryAddress("C", constructJson(0)).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.dismiss()
                val parser = Gson().fromJson(content, AddressListParser::class.java)
                if (!parser.error!!) {
                    addressList = parser.deliveryAddressList as ArrayList<DeliveryAddressList?>
                    addressList?.get(0)?.isSelected = true
                    deliveryAddressId = addressList?.get(0)?.id
                    textAddress?.text = addressList?.get(0)?.fullAddress
                    choosenAddress = addressList?.get(0)?.fullAddress.toString()
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(this@ConfirmOrderUserActivity, message)
            }
        })
    }

    override fun clickOnAddressItem(data: DeliveryAddressList?, position: Int) {
        if (addressDialog != null) {
            addressDialog?.dismiss()
            for (model in addressList!!) {
                model?.isSelected = model?.id == data?.id
            }
            deliveryAddressId = data?.id
            textAddress?.text = data?.fullAddress
            choosenAddress = data?.fullAddress.toString()
        }
    }

    override fun clickOnAddressItem(data: DeliveryAddressList?, position: Int, id: Any?) {}
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
                    model.unitType = "" + productList.seletecdUnitType
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
                `object`.addProperty("localId", localId)
                `object`.addProperty("totalAmt", totalAmt)
                `object`.addProperty("subTotal", subTotal)
                `object`.addProperty("deliveryCharge", deliveryCharge)
                `object`.addProperty("deliveryType", deliveryType)
                `object`.addProperty("deliveryTypeTime", deliveryTypeTime)
                `object`.addProperty("custId", custId)
                `object`.addProperty("sellerId", sellerId)
                `object`.addProperty("totalItems", totalItems)
                `object`.addProperty("deliveryAddressId", deliveryAddressId)
                `object`.addProperty("promoCodeId", promoCodeId)
                Log.e("Sabari", `object`.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return `object`
        }

    private fun callPlaceOrderApi() {
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.postPlaceOrder("C", params).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.dismiss()
                var error = false
                var message = ""
                var orderId = 0
                Log.e("ConfirmOrder", content.toString())
                try {
                    val json = JSONObject(content)
                    error = json.getBoolean("error")
                    message = json.getString("message")
                    orderId = json.getInt("orderId")
                    if (!error) {
                        Toast.makeText(this@ConfirmOrderUserActivity, message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ConfirmOrderUserActivity, PaymentActivity::class.java)
                        intent.putExtra("order_id", orderId)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.e(TAG, content)
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.dismiss()
            }
        })
    }

    private val paramsCheckQty: JsonObject
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
            Log.e(TAG, `object`.toString())
            return `object`
        }

    private fun callCheckQtyApi() {
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.postCheckProductQty("C", paramsCheckQty).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.dismiss()
                callPlaceOrderApi()
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
                        (unitDetail?.unitPrice?.toDouble()!! * item.qty)
                    } else {
                        unitDetail?.offerPrice?.toDouble()!! * item.qty
                    }
                }
                break
            }
            totalAmt = totalAmt + qtyAmt
        }
        this.totalAmt = totalAmt + deliveryCharge
        textSubTotal!!.text = totalAmt.toString() + ""
        textTotal!!.text = this.totalAmt.toString() + ""
        CustConstants.subTotal = totalAmt
        CustConstants.totalAmt = totalAmt + deliveryCharge
    }
}
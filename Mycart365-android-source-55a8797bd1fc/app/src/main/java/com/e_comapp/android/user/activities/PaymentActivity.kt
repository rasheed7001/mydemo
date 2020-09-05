package com.e_comapp.android.user.activities

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.CustConstants
import com.e_comapp.android.user.activities.PaymentActivity
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomEditText
import com.e_comapp.android.views.CustomTextView
import com.google.gson.Gson
import com.google.gson.JsonObject
import net.one97.paytm.nativesdk.PaytmSDK
import net.one97.paytm.nativesdk.app.PaytmSDKCallbackListener
import net.one97.paytm.nativesdk.common.widget.PaytmConsentCheckBox
import net.one97.paytm.nativesdk.dataSource.PaytmPaymentsUtilRepository
import net.one97.paytm.nativesdk.transcation.model.TransactionInfo
import okhttp3.ResponseBody
import retrofit2.Call

class PaymentActivity : BaseActivity(), PaytmSDKCallbackListener {
    var layoutCOD: ConstraintLayout? = null
    var layoutCard: ConstraintLayout? = null
    var layoutCardForm: ConstraintLayout? = null
    var layoutGPay: ConstraintLayout? = null
    var layoutPaytm: ConstraintLayout? = null
    var layoutPaytmProceed: ConstraintLayout? = null
    var text1: CustomTextView? = null
    var text2: CustomTextView? = null
    var textTotal: CustomTextView? = null
    var round1: View? = null
    var round2: View? = null
    var round3: View? = null
    var round4: View? = null
    var layoutCardRoot: LinearLayout? = null
    var layoutPayTmRoot: LinearLayout? = null
    var cbSaveCard: CheckBox? = null
    var etName: CustomEditText? = null
    var etCardNo: CustomEditText? = null
    var etExpireDate: CustomEditText? = null
    var etCCV: CustomEditText? = null
    var paytmConsentCheckBox: PaytmConsentCheckBox? = null
    var btnProceedWithPaytm: CustomBtn? = null
    var btnContinue: CustomBtn? = null
    var orderId = 0
    var paymentsUtilRepository: PaytmPaymentsUtilRepository? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        init()
        setupDefaults()
        setupEvents()
    }

    override fun menuClicked() {
        super.menuClicked()
        onBackPressed()
    }

    override fun rightMenuClicked() {
        super.rightMenuClicked()
        val intent = Intent(this@PaymentActivity, MyCartActivity::class.java)
        startActivity(intent)
    }

    private fun init() {
        textTotal = findViewById(R.id.txtTotal)
        layoutCOD = findViewById(R.id.cl_cod)
        layoutCard = findViewById(R.id.cl_card)
        layoutCardForm = findViewById(R.id.cl_card_form)
        layoutGPay = findViewById(R.id.cl_gpay)
        layoutPaytm = findViewById(R.id.cl_paytm)
        layoutPaytmProceed = findViewById(R.id.cl_paytm_confirm)
        layoutPayTmRoot = findViewById(R.id.paytm_root)
        text1 = findViewById(R.id.text1)
        text2 = findViewById(R.id.text2)
        round1 = findViewById(R.id.view_selected1)
        round2 = findViewById(R.id.view_selected2)
        round3 = findViewById(R.id.view_selected3)
        round4 = findViewById(R.id.view_selected4)
        layoutCardRoot = findViewById(R.id.card_root)
        etName = findViewById(R.id.et_name_on_card)
        etCardNo = findViewById(R.id.et_name_on_card)
        etExpireDate = findViewById(R.id.et_expire_date)
        etCCV = findViewById(R.id.et_ccv)
        /** paytm integration */
        paytmConsentCheckBox = findViewById(R.id.paytmConsentCheckBox)
        btnProceedWithPaytm = findViewById(R.id.btnProceedPaytm)
        btnContinue = findViewById(R.id.btnContinue)
        paymentsUtilRepository = PaytmSDK.getPaymentsUtilRepository()
    }

    private fun setupDefaults() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        setRightMenuIcon(R.drawable.my_cart_white)
        setCustTitle(getString(R.string.str_payment))
        if (intent != null && intent.hasExtra("order_id")) {
            orderId = intent.getIntExtra("order_id", 0)
        }
        textTotal!!.text = CustConstants.totalAmt.toString() + ""
    }

    private fun setupEvents() {
        layoutCOD!!.setOnClickListener { view -> select(view) }
        layoutCard!!.setOnClickListener { view -> select(view) }
        layoutGPay!!.setOnClickListener { view -> select(view) }
        layoutPaytm!!.setOnClickListener { view -> select(view) }
        btnProceedWithPaytm!!.setOnClickListener {
            if (paytmConsentCheckBox!!.isChecked) {
                FetchAuthCodeTask(this@PaymentActivity).execute()
            }
        }
        btnContinue!!.setOnClickListener { completeOrderTransaction() }
    }

    private fun select(viewGroup: View) {
        val id = viewGroup.id
        when (id) {
            R.id.cl_cod -> {
                layoutCOD!!.isSelected = true
                text1!!.isSelected = true
                round1!!.isSelected = true
                layoutCard!!.isSelected = false
                text2!!.isSelected = false
                round2!!.isSelected = false
                layoutCardForm!!.visibility = View.GONE
                layoutGPay!!.isSelected = false
                round3!!.isSelected = false
                layoutPaytm!!.isSelected = false
                layoutPaytmProceed!!.visibility = View.GONE
                round4!!.isSelected = false
            }
            R.id.cl_card -> {
                layoutCOD!!.isSelected = false
                text1!!.isSelected = false
                round1!!.isSelected = false
                layoutCard!!.isSelected = true
                text2!!.isSelected = true
                round2!!.isSelected = true
                layoutCardForm!!.visibility = View.VISIBLE
                layoutGPay!!.isSelected = false
                round3!!.isSelected = false
                layoutPaytm!!.isSelected = false
                layoutPaytmProceed!!.visibility = View.GONE
                round4!!.isSelected = false
            }
            R.id.cl_gpay -> {
                layoutCOD!!.isSelected = false
                text1!!.isSelected = false
                round1!!.isSelected = false
                layoutCard!!.isSelected = false
                text2!!.isSelected = false
                round2!!.isSelected = false
                layoutCardForm!!.visibility = View.GONE
                layoutGPay!!.isSelected = true
                round3!!.isSelected = true
                layoutPaytm!!.isSelected = false
                layoutPaytmProceed!!.visibility = View.GONE
                round4!!.isSelected = false
            }
            R.id.cl_paytm -> {
                layoutCOD!!.isSelected = false
                text1!!.isSelected = false
                round1!!.isSelected = false
                layoutCard!!.isSelected = false
                text2!!.isSelected = false
                round2!!.isSelected = false
                layoutCardForm!!.visibility = View.GONE
                layoutGPay!!.isSelected = false
                round3!!.isSelected = false
                layoutPaytm!!.isSelected = true
                layoutPaytmProceed!!.visibility = View.VISIBLE
                round4!!.isSelected = true
            }
        }
    }

    private fun constructJson(): JsonObject {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("orderId", orderId)
            jsonObject.addProperty("transactionDetails", "test details")
            jsonObject.addProperty("transactionId", "123")
            jsonObject.addProperty("paymentType", "1")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    private fun completeOrderTransaction() {
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.postCompleteOrderTransaction("C", constructJson()).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.dismiss()
                Log.e("PaymentActivity", content)
                val intent = Intent(this@PaymentActivity, MapActivity::class.java)
                startActivity(intent)
                CustConstants.selectedProdList.clear()
                CustConstants.subTotal = 0.0
                CustConstants.totalAmt = 0.0
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.dismiss()
            }
        })
    }

    override fun onTransactionResponse(transactionInfo: TransactionInfo) {
        if (transactionInfo != null) {
            if (transactionInfo.txnInfo != null) {
                val s = Gson().toJson(transactionInfo.txnInfo)
                Toast.makeText(this, s, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun networkError() {}
    override fun onBackPressedCancelTransaction() {}
    override fun onGenericError(i: Int, s: String) {}

    internal inner class FetchAuthCodeTask(var mContext: Context) : AsyncTask<Context?, Int?, String?>() {
        protected override fun doInBackground(vararg contexts: Context?): String? {
            return paymentsUtilRepository!!.fetchAuthCode(mContext, "TldgzY05826369404961")
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            Log.e("AuthCode", s)
        }

    }
}
package com.e_comapp.android.seller.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import com.e_comapp.android.BuildConfig
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.activities.OtpActivity
import com.e_comapp.android.seller.adapters.ImagePagerAdapter
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.activities.UserSignupActivity
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.views.CustomTextView
import com.e_comapp.android.views.OtpEditText
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import pl.pzienowicz.autoscrollviewpager.AutoScrollViewPager
import retrofit2.Call
import retrofit2.Response
import java.util.*

class OtpActivity : BaseActivity() {
    var TAG = javaClass.name
    var etOtp: OtpEditText? = null
    var spannableString: SpannableString? = null
    var textResend: CustomTextView? = null
    var viewPager: AutoScrollViewPager? = null
    var pagerAdapter: ImagePagerAdapter? = null
    var imageList: MutableList<Int>? = null
    var currentPage = 0
    var timer: Timer? = null
    val DELAY_MS = 500 //delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_user)
        init()
        setupDefaults()
        setupEvents()
    }

    private fun init() {
        viewPager = findViewById(R.id.viewPager)
        imageList = ArrayList()
        imageList?.add(R.drawable.vehicle_red)
        imageList?.add(R.drawable.wallet)
        pagerAdapter = ImagePagerAdapter(this, imageList as ArrayList<Int>)
        textResend = findViewById(R.id.textResend)
        spannableString = SpannableString(getString(R.string.str_otp_sent_resend_in_20_secs))
        spannableString!!.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {}
        }, 9, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString!!.setSpan(StyleSpan(Typeface.BOLD), 8, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textResend?.setText(spannableString)
        textResend?.setMovementMethod(LinkMovementMethod.getInstance())
        etOtp = findViewById(R.id.etOtp)
    }

    private fun setupDefaults() {
        viewPager!!.adapter = pagerAdapter
        viewPager!!.startAutoScroll(DELAY_MS)

//        AlertUtils.showAlert(OtpActivity.this,getApp().getAppPreference().getOtp()+"");
        etOtp!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                var otp: String
                if (etOtp!!.text.toString().also { otp = it }.length == 6) {
                    if (BuildConfig.IS_CUST) {
                        callOtpVerify(otp)
                    } else {
                        callSellerOtpVerify(otp)
                    }

                    /*   startActivity(new Intent(OtpUserActivity.this,SignupUserActivity.class));
                    finish();*/
                }
            }
        })
    }

    private fun setupEvents() {}
    var userType = "S"
    private fun constructJson(otp: String): JsonObject {
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("otp", otp)
            jsonObject.addProperty("platform", "1")
            userType = if (BuildConfig.IS_CUST) {
                jsonObject.addProperty("userType", "C")
                "C"
            } else {
                jsonObject.addProperty("userType", "S")
                "S"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    private fun callOtpVerify(otp: String) {
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.postCustOtpVerify("C", constructJson(otp)).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.hide()
                Log.e(TAG, content)
                val jsonObject: JSONObject
                var isError = false
                val message: String
                try {
                    jsonObject = JSONObject(content)
                    isError = jsonObject.getBoolean("error")
                    message = jsonObject.getString("message")
                    AlertUtils.showToast(this@OtpActivity, message)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if (!isError) {
                    startActivity(Intent(this@OtpActivity, UserSignupActivity::class.java))
                    finish()
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(this@OtpActivity, message)
            }
        })
    }

    private fun callSellerOtpVerify(otp: String) {
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.postSellerOtpVerify("S", constructJson(otp)).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.hide()
                Log.e(TAG, content)
                val jsonObject: JSONObject
                var isError = false
                val message: String
                try {
                    jsonObject = JSONObject(content)
                    isError = jsonObject.getBoolean("error")
                    message = jsonObject.getString("message")
                    AlertUtils.showToast(this@OtpActivity, message)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if (!isError) {
                    startActivity(Intent(this@OtpActivity, SignupActivity::class.java))
                    finish()
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(this@OtpActivity, message)
            }
        })
    }
}
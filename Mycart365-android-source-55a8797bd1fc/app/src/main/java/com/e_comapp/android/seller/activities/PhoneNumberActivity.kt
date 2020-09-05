package com.e_comapp.android.seller.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.e_comapp.android.BuildConfig
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.activities.PhoneNumberActivity
import com.e_comapp.android.seller.adapters.ImagePagerAdapter
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.seller.models.SellerRegParser
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import pl.pzienowicz.autoscrollviewpager.AutoScrollViewPager
import retrofit2.Call
import java.util.*

class PhoneNumberActivity : BaseActivity() {
    val TAG = javaClass.name
    private var errorFlag = false
    var btnSendOtp: CustomBtn? = null
    var etPhoneNumber: CustomEditText? = null
    var viewPager: AutoScrollViewPager? = null
    var pagerAdapter: ImagePagerAdapter? = null
    var imageList: MutableList<Int>? = null
    var currentPage = 0
    var timer: Timer? = null
    val DELAY_MS = 500 //delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number_user)
        init()
        setupDefaults()
        setupEvents()
    }

    private fun init() {
        etPhoneNumber = findViewById(R.id.et_phone_number)
        btnSendOtp = findViewById(R.id.btn_send_otp)
        viewPager = findViewById(R.id.viewPager)
        imageList = ArrayList()
        imageList?.add(R.drawable.vehicle_red)
        imageList?.add(R.drawable.wallet)
        pagerAdapter = ImagePagerAdapter(this, imageList as ArrayList<Int>)
    }

    private fun setupDefaults() {
        viewPager!!.adapter = pagerAdapter
        viewPager!!.startAutoScroll(DELAY_MS)
        /* */ /*After setting the adapter use the timer */ /*
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == 1) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);*/
    }

    private fun setupEvents() {
        btnSendOtp!!.setOnClickListener {
            val number = etPhoneNumber!!.text.toString()
            errorFlag = if (number.isEmpty()) {
                etPhoneNumber!!.setHintTextColor(resources.getColor(R.color.warning_red))
                true
            } else {
                false
            }
            if (!errorFlag) {
                if (BuildConfig.IS_CUST) {
                    callRegistrationApi()
                } else {
                    callSellerRegistrationApi()
                }
                /* Intent intent = new Intent(PhoneNumberUserActivity.this,OtpUserActivity.class);
                    startActivity(intent);
                    finish();*/
            }
        }
    }

    var mobile = ""
    private fun constructJson(mobile: String, platform: String): JsonObject {
        this.mobile = mobile
        val jsonObject = JsonObject()
        try {
            jsonObject.addProperty("mobile", mobile)
            jsonObject.addProperty("platform", platform)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    private fun callRegistrationApi() {
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.postCustReg(constructJson(etPhoneNumber!!.text.toString(), "1")).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.hide()
                Log.e(TAG, content)
                val parser = Gson().fromJson(content, SellerRegParser::class.java)
                app.appPreference?.setOtpStaus(parser.userDetails?.otpStatus)
                app.appPreference?.setStatus(parser.userDetails?.status)
                app.appPreference?.setAccessToken(parser.userDetails!!.accessToken)
                app.appPreference?.setAppUserId(parser.userDetails!!.id)
                parser?.otp?.let { app.appPreference?.setOtp(it) }
                app.appPreference?.setMobileNumber(mobile)
                val intent = Intent(this@PhoneNumberActivity, OtpActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(this@PhoneNumberActivity, message)
            }
        })
    }

    private fun callSellerRegistrationApi() {
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        app.retrofitInterface.postSellerReg(constructJson(etPhoneNumber!!.text.toString(), "1")).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.hide()
                Log.e(TAG, content)
                val parser = Gson().fromJson(content, SellerRegParser::class.java)
                app.appPreference!!.setOtpStaus(parser.userDetails?.otpStatus)
                app.appPreference!!.setStatus(parser.userDetails?.status)
                app.appPreference!!.setAccessToken(parser.userDetails?.accessToken)
                parser?.otp?.let { app.appPreference!!.setOtp(it) }
                val intent = Intent(this@PhoneNumberActivity, OtpActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(this@PhoneNumberActivity, message)
            }
        })
    }

    override fun onDestroy() {
        dialog!!.dismiss()
        super.onDestroy()
    }
}
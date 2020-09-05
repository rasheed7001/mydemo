package com.e_comapp.android.seller.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.e_comapp.android.BuildConfig
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.seller.models.AppDefaultsParser
import com.e_comapp.android.utils.AlertUtils
import com.e_comapp.android.utils.DeviceUtils
import com.e_comapp.android.utils.TextUtils
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call

class SplashActivity : BaseActivity() {
    val TAG = javaClass.name
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)
        init()
        setupDefaults()
        setupeEvensts()
        Handler().postDelayed({ callAppDataApi() }, 3000)
    }

    private fun init() {}
    private fun setupDefaults() {}
    private fun setupeEvensts() {}

    private fun callAppDataApi() {
//        dialog!!.show()
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }

        app.retrofitInterface.getAppDefaults()?.enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
//                dialog!!.hide()
                Log.e(TAG, content)
                app.appPreference?.setAppDefaults(content)
                val parser = Gson().fromJson(content, AppDefaultsParser::class.java)
                checkLogin()
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.hide()
                AlertUtils.showAlert(this@SplashActivity, message)
            }
        })
    }

    private fun redirectHome() {

        if (BuildConfig.FLAVOR.equals("seller")) {
            startActivity(Intent(this@SplashActivity, MainPageActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this@SplashActivity, com.e_comapp.android.user.activities.MainPageActivity::class.java))
            finish()
        }

    }

    private fun redirectLogin() {
        startActivity(Intent(this@SplashActivity, PhoneNumberActivity::class.java))
        finish()
    }

    private fun checkLogin() {
        if (TextUtils.isNullOrEmpty(app.appPreference!!.getAppAccessToken())) {
            redirectLogin()
        } else {
            redirectHome()
        }
    }
}
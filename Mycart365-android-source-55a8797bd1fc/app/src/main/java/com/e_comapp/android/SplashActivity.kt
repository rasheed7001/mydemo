package com.e_comapp.android

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.activities.MainPageActivity
import com.e_comapp.android.seller.activities.PhoneNumberActivity
import com.e_comapp.android.utils.TextUtils

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)
        Handler().postDelayed({ checkLogin() }, 5000)
    }

    private fun redirectHome() {
        startActivity(Intent(this@SplashActivity, MainPageActivity::class.java))
        finish()
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
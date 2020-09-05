package com.e_comapp.android.user.activities

import android.os.Bundle
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity

class BuyAnythingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_anything)
        init()
        setupDefaults()
        setupEvents()
    }

    private fun init() {}
    private fun setupDefaults() {}
    private fun setupEvents() {}
}
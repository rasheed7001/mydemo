package com.e_comapp.android.seller.activities

import android.os.Bundle
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity

class UpdateProductActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_product)
        init()
        setupDefaults()
        setupEvents()
    }

    private fun init() {}
    private fun setupDefaults() {}
    private fun setupEvents() {}
}
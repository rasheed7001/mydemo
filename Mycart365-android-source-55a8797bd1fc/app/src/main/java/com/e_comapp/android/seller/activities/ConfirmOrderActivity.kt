package com.e_comapp.android.seller.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.activities.ConfirmOrderActivity
import com.e_comapp.android.seller.adapters.AddedItemsAdapters
import com.e_comapp.android.views.CustomBtn

class ConfirmOrderActivity : BaseActivity() {
    var recyclerView: RecyclerView? = null
    var adapter: AddedItemsAdapters? = null
    var btnProcess: CustomBtn? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)
        init()
        setupDefaults()
        setupEvents()
    }

    private fun init() {
        btnProcess = findViewById(R.id.btnProcess)
        recyclerView = findViewById(R.id.rv_items)
        adapter = AddedItemsAdapters(R.color.app_bg_blue, this)
    }

    private fun setupEvents() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        setCustTitle(getString(R.string.str_confirm_order))
        btnProcess!!.setOnClickListener {
            val intent = Intent(this@ConfirmOrderActivity, TrackActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupDefaults() {
        recyclerView!!.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView!!.adapter = adapter
    }
}
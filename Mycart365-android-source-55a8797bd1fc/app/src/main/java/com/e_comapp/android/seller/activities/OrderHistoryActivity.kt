package com.e_comapp.android.seller.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.adapters.OrderHistoryRecyclerAdapter
import com.e_comapp.android.seller.adapters.OrderHistoryRecyclerAdapter.OnOrderHistoryClickedListener

class OrderHistoryActivity : BaseActivity(), OnOrderHistoryClickedListener {
    var recyclerView: RecyclerView? = null
    var adapter: OrderHistoryRecyclerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        init()
        setupDefaults()
        setupEvents()
    }

    private fun init() {
        recyclerView = findViewById(R.id.rv_order_history)
        adapter = OrderHistoryRecyclerAdapter(this)
    }

    private fun setupDefaults() {
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        setTitle(getString(R.string.str_order_history))
        recyclerView!!.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView!!.adapter = adapter
    }

    private fun setupEvents() {}
    override fun menuClicked() {
        super.menuClicked()
        onBackPressed()
    }

    override fun onOrderHistoryClicked() {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        startActivity(intent)
    }
}
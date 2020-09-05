package com.e_comapp.android.seller.activities

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.adapters.ViewPagerAdapter
import com.e_comapp.android.seller.fragments.ItemsFragment
import com.e_comapp.android.seller.fragments.SummaryFragment
import com.google.android.material.tabs.TabLayout

class OrderDetailsActivity : BaseActivity() {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details2)
        init()
        setupDefaults()
        setupEvents()
    }

    private fun init() {
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
    }

    private fun setupDefaults() {
        setupViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)
    }

    private fun setupEvents() {}
    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(SummaryFragment(), resources.getString(R.string.str_summary))
        adapter.addFragment(ItemsFragment(), resources.getString(R.string.str_items))
        viewPager!!.adapter = adapter
    }
}
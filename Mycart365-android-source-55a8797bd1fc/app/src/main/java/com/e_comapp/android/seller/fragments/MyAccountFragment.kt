package com.e_comapp.android.seller.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseFragment
import com.e_comapp.android.seller.activities.MainPageActivity
import com.e_comapp.android.seller.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout

class MyAccountFragment : BaseFragment() {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setLeftMenuIcon(R.drawable.ic_left_menu)
        setTitle(getString(R.string.str_my_account))
    }

    override fun menuClicks() {
        super.menuClicks()
        (requireActivity() as MainPageActivity).getDrawer()?.openDrawer(GravityCompat.START)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)
        viewPager = view.findViewById<View>(R.id.viewPager) as ViewPager
        setupViewPager(viewPager)
        tabLayout = view.findViewById<View>(R.id.tabLayout) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)
        return view
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(CompanyInfoFragment(), resources.getString(R.string.str_company))
        adapter.addFragment(ContactInfoFragment(), resources.getString(R.string.str_contact))
        adapter.addFragment(PaymentInfoFragment(), resources.getString(R.string.str_payment))
        viewPager!!.adapter = adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }
}
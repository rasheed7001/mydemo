package com.e_comapp.android.seller.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseFragment
import com.e_comapp.android.seller.adapters.SellerListAdapter

class SellerListFragment : BaseFragment() {
    private var rvSellerListView: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_seller_list, container, false)
        rvSellerListView = view.findViewById(R.id.rvSellerListView)
        val listAdapter = SellerListAdapter(activity)
        rvSellerListView?.layoutManager = LinearLayoutManager(activity)
        rvSellerListView?.adapter = listAdapter
        return view
    }
}
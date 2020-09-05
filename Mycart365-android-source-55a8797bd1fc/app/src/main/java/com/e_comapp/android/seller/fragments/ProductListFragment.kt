package com.e_comapp.android.seller.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseFragment
import com.e_comapp.android.seller.adapters.ProductListAdapter

class ProductListFragment : BaseFragment() {
    private var rvSellerListView: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_product_list, container, false)
        rvSellerListView = view.findViewById(R.id.rvListView)
        val listAdapter = ProductListAdapter(activity)
        rvSellerListView?.layoutManager = GridLayoutManager(activity, 2)
        rvSellerListView?.adapter = listAdapter
        return view
    }
}
package com.e_comapp.android.user

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.databinding.DialogDeliveryAddressBinding
import com.e_comapp.android.databinding.DialogDeliveryTypeBinding
import com.e_comapp.android.user.adapters.DeliveryAddressAdapter
import com.e_comapp.android.views.BaseBottomSheetDialogFragment

class DeliveryAddressDialog(adapter: DeliveryAddressAdapter, address: String?) : BaseBottomSheetDialogFragment() {

    var activity: Context? = null
    var yes: Button? = null
    var no: Button? = null
    var recyclerView: RecyclerView? = null
    var address: String? = address
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    var adapter: DeliveryAddressAdapter = adapter

    private lateinit var binding: DialogDeliveryAddressBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.dialog_delivery_address,
                container,
                false
        )

        binding.lifecycleOwner = this

        binding.rvDeliveryAddress.adapter = adapter

        return binding.root    }

    override fun setActionListener() {

    }
}
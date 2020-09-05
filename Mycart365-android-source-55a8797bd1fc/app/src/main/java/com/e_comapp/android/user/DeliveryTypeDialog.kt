package com.e_comapp.android.user

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.databinding.DialogDeliveryTypeBinding
import com.e_comapp.android.user.adapters.DeliveryTypeAdapter
import com.e_comapp.android.views.BaseBottomSheetDialogFragment
import com.e_comapp.android.views.CustomTextView

class DeliveryTypeDialog(context: Context,adapter: DeliveryTypeAdapter, address: String?) : BaseBottomSheetDialogFragment() {

    private lateinit var binding: DialogDeliveryTypeBinding
    var activity: Context? = null
    var yes: Button? = null
    var no: Button? = null
    var textAddress: CustomTextView? = null
    var recyclerView: RecyclerView? = null
    var address: String? = address
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    var adapter: DeliveryTypeAdapter = adapter

    override fun setActionListener() {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                layoutInflater,
                R.layout.dialog_delivery_type,
                container,
                false
        )

        binding.lifecycleOwner = this


        binding.textAddress.text = address
        binding.rvDeliveryType.adapter = adapter

        return binding.root
    }
}
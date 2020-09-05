package com.e_comapp.android.user.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.user.models.AddressListParser.DeliveryAddressList
import com.e_comapp.android.views.CustomTextView
import java.util.*

class DeliveryAddressAdapter : RecyclerView.Adapter<DeliveryAddressAdapter.UnitTypeViewHolder> {
    private var mDataset: ArrayList<DeliveryAddressList?>?
    var recyclerViewItemClickListener: DeliveryAddressClickListener
    private val selectedProductPosition = 0
    var id: Any? = null
    private var context: Context

    constructor(context: Context, myDataset: ArrayList<DeliveryAddressList?>?, listener: DeliveryAddressClickListener) {
        mDataset = myDataset
        recyclerViewItemClickListener = listener
        this.context = context
    }

    constructor(context: Context, myDataset: ArrayList<DeliveryAddressList?>?, listener: DeliveryAddressClickListener, id: Any?) {
        mDataset = myDataset
        recyclerViewItemClickListener = listener
        this.context = context
        this.id = id
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): UnitTypeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.delivery_address_recycler_item, parent, false)
        return UnitTypeViewHolder(v, i)
    }

    override fun onBindViewHolder(UnitTypeViewHolder: UnitTypeViewHolder, i: Int) {
        UnitTypeViewHolder.mTextView.text = mDataset!![i]?.fullAddress
        UnitTypeViewHolder.textType.text = mDataset!![i]?.addressType
        if (mDataset!![i]!!.isSelected) {
            UnitTypeViewHolder.llroot.setBackgroundResource(R.drawable.rounded_corner_blue)
            UnitTypeViewHolder.llType.setBackgroundResource(R.drawable.rounded_bg_white_stroke)
            UnitTypeViewHolder.mTextView.isSelected = true
        } else {
            UnitTypeViewHolder.llroot.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            UnitTypeViewHolder.llType.setBackgroundResource(R.drawable.rounded_grey_stroke)
            UnitTypeViewHolder.mTextView.isSelected = false
        }
    }

    override fun getItemCount(): Int {
        return mDataset!!.size
    }

    inner class UnitTypeViewHolder(v: View, position: Int) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var mTextView: CustomTextView
        var textType: CustomTextView
        var llroot: LinearLayout
        var llType: LinearLayout
        var imgIcon: ImageView
        override fun onClick(v: View) {
            if (id != null) {
                recyclerViewItemClickListener.clickOnAddressItem(mDataset!![this.adapterPosition], position, id)
            } else {
                recyclerViewItemClickListener.clickOnAddressItem(mDataset!![this.adapterPosition], position)
            }
        }

        init {
            mTextView = v.findViewById<View>(R.id.textAddress) as CustomTextView
            textType = v.findViewById(R.id.textType)
            imgIcon = v.findViewById(R.id.imgIcon)
            llType = v.findViewById(R.id.ll_type)
            llroot = v.findViewById(R.id.ll_root)
            v.setOnClickListener(this)
        }
    }

    interface DeliveryAddressClickListener {
        fun clickOnAddressItem(data: DeliveryAddressList?, position: Int)
        fun clickOnAddressItem(data: DeliveryAddressList?, position: Int, id: Any?)
    }
}
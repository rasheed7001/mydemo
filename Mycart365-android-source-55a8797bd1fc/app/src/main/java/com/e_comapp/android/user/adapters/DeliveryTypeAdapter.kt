package com.e_comapp.android.user.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.user.models.DeliveryTypeModel
import com.e_comapp.android.views.CustomTextView
import java.util.*

class DeliveryTypeAdapter(private val mDataset: ArrayList<DeliveryTypeModel>?, var recyclerViewItemClickListener: DeliveryTypeClickListener) : RecyclerView.Adapter<DeliveryTypeAdapter.UnitTypeViewHolder>() {
    private val selectedProductPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): UnitTypeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.unit_type_item, parent, false)
        return UnitTypeViewHolder(v, i)
    }

    override fun onBindViewHolder(UnitTypeViewHolder: UnitTypeViewHolder, i: Int) {
        UnitTypeViewHolder.mTextView.text = mDataset!![i].deliveryType
        UnitTypeViewHolder.mTextView.isSelected = mDataset[i].isSelected
    }

    override fun getItemCount(): Int {
        return mDataset!!.size
    }

    inner class UnitTypeViewHolder(v: View, position: Int) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var mTextView: CustomTextView
//        var position: Int
        override fun onClick(v: View) {
            recyclerViewItemClickListener.clickOnItem(mDataset!![this.adapterPosition], position)
        }

        init {
            mTextView = v.findViewById<View>(R.id.textUnitType) as CustomTextView
            v.setOnClickListener(this)
//            this.position = position
        }
    }

    interface DeliveryTypeClickListener {
        fun clickOnItem(data: DeliveryTypeModel, position: Int)
    }

}
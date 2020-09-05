package com.e_comapp.android.user.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.user.models.ProductListParser
import com.e_comapp.android.views.CustomTextView
import java.util.*

class UnitTypeAdapter(private val mDataset: ArrayList<ProductListParser.ProductList.UnitDetail?>?, var recyclerViewItemClickListener: RecyclerViewItemClickListener, private val selectedProductPosition: Int) : RecyclerView.Adapter<UnitTypeAdapter.UnitTypeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, i: Int): UnitTypeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.unit_type_item, parent, false)
        return UnitTypeViewHolder(v, i)
    }

    override fun onBindViewHolder(UnitTypeViewHolder: UnitTypeViewHolder, i: Int) {
        UnitTypeViewHolder.mTextView.text = mDataset!![i]?.unitType
    }

    override fun getItemCount(): Int {
        return mDataset!!.size
    }

    inner class UnitTypeViewHolder(v: View, position: Int) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var mTextView: CustomTextView
        override fun onClick(v: View) {
            recyclerViewItemClickListener.clickOnItem(mDataset!![this.adapterPosition], selectedProductPosition, position)
        }

        init {
            mTextView = v.findViewById<View>(R.id.textUnitType) as CustomTextView
            v.setOnClickListener(this)
       }
    }

    interface RecyclerViewItemClickListener {
        fun clickOnItem(data: ProductListParser.ProductList.UnitDetail?, position: Int, unitPos: Int)
    }

}
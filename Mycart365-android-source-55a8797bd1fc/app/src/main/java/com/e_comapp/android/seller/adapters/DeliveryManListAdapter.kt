package com.e_comapp.android.seller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R

class DeliveryManListAdapter(private val context: Context?) : RecyclerView.Adapter<DeliveryManListAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_delivery_man_list_item, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {}
    override fun getItemCount(): Int {
        return 15
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
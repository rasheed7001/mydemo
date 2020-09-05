package com.e_comapp.android.seller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.views.CustomTextView
import com.facebook.drawee.view.SimpleDraweeView

class OrderHistoryRecyclerAdapter(var orderHistoryClickedListener: OnOrderHistoryClickedListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemVeiw = LayoutInflater.from(parent.context).inflate(R.layout.order_history_recycler_item, parent, false)
        return HistoryViewHolder(itemVeiw, orderHistoryClickedListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HistoryViewHolder) {
            holder.bindView()
        }
    }

    override fun getItemCount(): Int {
        return 6
    }

    inner class HistoryViewHolder(itemView: View, listener: OnOrderHistoryClickedListener) : RecyclerView.ViewHolder(itemView) {
        var textDate: CustomTextView
        var textOrderId: CustomTextView
        var textPrice: CustomTextView
        var textQty: CustomTextView
        var textStatus: CustomTextView
        var textUserName: CustomTextView
        var imgUser: SimpleDraweeView
        var listener: OnOrderHistoryClickedListener
        fun bindView() {
            itemView.setOnClickListener { listener.onOrderHistoryClicked() }
        }

        init {
            textDate = itemView.findViewById(R.id.textPrice)
            textOrderId = itemView.findViewById(R.id.textItemName)
            textPrice = itemView.findViewById(R.id.textPrice)
            textQty = itemView.findViewById(R.id.textPrice)
            textStatus = itemView.findViewById(R.id.textStatus)
            textUserName = itemView.findViewById(R.id.textUserName)
            imgUser = itemView.findViewById(R.id.imgUser)
            this.listener = listener
        }
    }

    interface OnOrderHistoryClickedListener {
        fun onOrderHistoryClicked()
    }

}
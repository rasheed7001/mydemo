package com.e_comapp.android.user.adapters

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.user.listeners.OnSellerItemClickedListener
import com.e_comapp.android.user.models.SellerListParser.SellerList
import com.e_comapp.android.util.component.MyCartImageView
import com.e_comapp.android.views.CustomTextView
import com.facebook.drawee.view.SimpleDraweeView
import java.util.*

class SearchRecyclerAdapter(var listener: OnSellerItemClickedListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var location: Location? = null
    private var sellerList = emptyList<SellerList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemVeiw = LayoutInflater.from(parent.context).inflate(R.layout.search_recycler_item, parent, false)
        return AddedItemViewHolder(itemVeiw, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddedItemViewHolder) {
            holder.bindView(sellerList[position])
        }
    }

    override fun getItemCount(): Int {
        return sellerList.size
    }

    fun updateList(sellerList: List<SellerList>) {
        this.sellerList = sellerList
        notifyDataSetChanged()
    }

    inner class AddedItemViewHolder(itemView: View, listener: OnSellerItemClickedListener?) : RecyclerView.ViewHolder(itemView) {
        var imgLogo: MyCartImageView
        var textCompanyName: CustomTextView
        fun bindView(seller: SellerList?) {
            imgLogo.loadImageWithUrl(seller?.logo,R.drawable.login_logo,null)
            textCompanyName.text = seller?.companyName
            itemView.setOnClickListener { listener.onSellerItemClicked(seller) }
        }

        init {
            imgLogo = itemView.findViewById(R.id.imgLogo)
            textCompanyName = itemView.findViewById(R.id.textSellerName)
        }
    }

}
package com.e_comapp.android.seller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.seller.models.ProductList
import com.e_comapp.android.seller.models.StockListParser
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomTextView
import java.util.*

class StockListingRecyclerAdapter(var context: Context?, var productList: ArrayList<ProductList?>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemVeiw = LayoutInflater.from(parent.context).inflate(R.layout.stock_recycler_item, parent, false)
        return StockViewHolder(itemVeiw)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StockViewHolder) {
            holder.bindView(productList!![position])
        }
    }

    override fun getItemCount(): Int {
        return productList!!.size
    }

    fun updateList(list: ArrayList<ProductList?>?) {
        productList = list
        notifyDataSetChanged()
    }

    inner class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageItem: ImageView
        var textBrand: CustomTextView
        var textItemName: CustomTextView
        var textRating: CustomTextView
        var textRatingCount: CustomTextView
        var textQty: CustomTextView
        var textPrice: CustomTextView
        var textAvailability: CustomTextView
        var btnUpdate: CustomBtn
        var btnDelete: CustomBtn
        fun bindView(product: ProductList?) {
            textBrand.text = product?.brand
            textItemName.text = product?.productName
            textPrice.text = product?.unitDetails?.get(0)?.offerPrice
        }

        init {
            imageItem = itemView.findViewById(R.id.itemImage)
            textBrand = itemView.findViewById(R.id.textPrice)
            textItemName = itemView.findViewById(R.id.textItemName)
            textRating = itemView.findViewById(R.id.textRating1)
            textRatingCount = itemView.findViewById(R.id.textPrice)
            textQty = itemView.findViewById(R.id.textQty)
            textPrice = itemView.findViewById(R.id.textPrice)
            textAvailability = itemView.findViewById(R.id.textAvailability)
            btnUpdate = itemView.findViewById(R.id.btnUpdate)
            btnDelete = itemView.findViewById(R.id.btnDelete)
        }
    }

}
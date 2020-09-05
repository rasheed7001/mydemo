package com.e_comapp.android.user.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.user.CustConstants
import com.e_comapp.android.user.activities.ConfirmOrderUserActivity
import com.e_comapp.android.user.activities.MyCartActivity
import com.e_comapp.android.user.models.ProductListParser
import com.e_comapp.android.util.component.MyCartImageView
import com.e_comapp.android.views.CustomTextView
import com.facebook.drawee.view.SimpleDraweeView
import java.util.*

class MyCartRecyclerAdapter(var productList: ArrayList<ProductListParser.ProductList?>?, var context: Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var activity: BaseActivity? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemVeiw = LayoutInflater.from(parent.context).inflate(R.layout.add_recycler_item, parent, false)
        return AddedItemViewHolder(itemVeiw)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddedItemViewHolder) {
            holder.bindView(productList!![position], position)
        }
    }

    override fun getItemCount(): Int {
        return productList!!.size
    }

    fun updateList(productList: ArrayList<ProductListParser.ProductList?>?) {
        this.productList = productList
        notifyDataSetChanged()
    }

    inner class AddedItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img: MyCartImageView
        var textProductName: CustomTextView
        var textUnitType: CustomTextView
        var textPrice: CustomTextView
        var textCount: CustomTextView
        var imgPlus: ImageView
        var imgMinus: ImageView
        fun bindView(product: ProductListParser.ProductList?, position: Int) {
            textProductName.text = product?.productName
            if (product?.unitDetails?.isNotEmpty()!!) {
                val unitDetail = product.unitDetails?.get(0)
                if (unitDetail?.offerPrice != "0.0" && unitDetail?.offerPrice != "") {
                    textPrice.text = unitDetail?.offerPrice
                } else {
                    textPrice.text = unitDetail?.unitPrice
                }
            }
            if (!product.prodImages?.isEmpty()!!) img.loadImageWithUrl(product.prodImages?.get(0)?.imgUrl, R.drawable.image_placeholder, null)
            if (!product.seletecdUnitType.isEmpty()) textUnitType.text = product.seletecdUnitType
            textCount.text = product.qty.toString() + " "
            imgPlus.setOnClickListener {
                addQty(position)
                if (activity != null) {
                    if (activity is MyCartActivity) {
                        (activity as MyCartActivity).calculateAmt()
                    } else if (activity is ConfirmOrderUserActivity) {
                        (activity as ConfirmOrderUserActivity).calculateAmt()
                    }
                }
            }
            imgMinus.setOnClickListener {
                deleteQty(position)
                if (activity != null) {
                    if (activity is MyCartActivity) {
                        (activity as MyCartActivity).calculateAmt()
                    } else if (activity is ConfirmOrderUserActivity) {
                        (activity as ConfirmOrderUserActivity).calculateAmt()
                    }
                }
            }
        }

        private fun addQty(pos: Int) {
            var isAvailable = false
            for (item in CustConstants.selectedProdList) {
                if (item?.id.equals("" + productList!![pos]?.id, ignoreCase = true)) {
                    isAvailable = true
                    for (unitDetail in item?.unitDetails!!) {
                        if (unitDetail?.id.equals(item.seletecdUnitId, ignoreCase = true)) {
                            if (item.qty < unitDetail?.availStock?.toInt()!!) {
                                item.qty += 1
                                textCount.text = "" + item.qty
                                productList!![pos] = item
                                Log.e("ProdList", item.productName + " " + item.qty)
                            } else {
                                Toast.makeText(context, "Maximum Quantity Exceed", Toast.LENGTH_SHORT).show()
                            }
                        }
                        break
                    }
                }
            }
            if (!isAvailable) {
                val parser = ProductListParser(productList!![pos])
                parser.product!!.qty = 1
                textCount.text = parser.product!!.qty.toString() + ""
                CustConstants.selectedProdList.add(parser.product)
            }
            notifyItemChanged(pos)
        }

        private fun deleteQty(pos: Int) {
            for (item in CustConstants.selectedProdList) {
                if (item?.id.equals("" + productList!![pos]?.id, ignoreCase = true)) {
                    if (item!!.qty > 1) {
                        item.qty -= 1
                    } else {
                        item.qty = 0
                    }
                    productList!![pos] = item
                    textCount.text = "" + item.qty
                    if (item.qty == 0) {
                    }
                    break
                }
            }
            deleteZeroQtyItems()
            notifyItemChanged(pos)
        }

        private fun deleteZeroQtyItems() {
            for (item in CustConstants.selectedProdList) {
                if (item!!.qty == 0) {
                    CustConstants.selectedProdList.remove(item)
                    productList!!.remove(item)
                    notifyDataSetChanged()
                    break
                }
            }
        }

        init {
            img = itemView.findViewById(R.id.imgItem)
            textProductName = itemView.findViewById(R.id.textItemName)
            textUnitType = itemView.findViewById(R.id.textQty)
            textPrice = itemView.findViewById(R.id.textPrice)
            textCount = itemView.findViewById(R.id.textCount)
            imgPlus = itemView.findViewById(R.id.imgPlus)
            imgMinus = itemView.findViewById(R.id.imgMinus)
        }
    }

}
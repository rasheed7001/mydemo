package com.e_comapp.android.user.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.user.CustConstants
import com.e_comapp.android.user.CustomListViewDialog
import com.e_comapp.android.user.adapters.UnitTypeAdapter.RecyclerViewItemClickListener
import com.e_comapp.android.user.fragments.ProductListFragment
import com.e_comapp.android.user.models.ProductListParser
import com.e_comapp.android.util.component.MyCartImageView
import com.e_comapp.android.utils.TextUtils
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomTextView
import java.util.*


class ProductListRecyclerAdapter(var context: Context?, var productList: ArrayList<ProductListParser.ProductList?>?, private val fragment: ProductListFragment?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemVeiw = LayoutInflater.from(parent.context).inflate(R.layout.product_recycler_item2, parent, false)
        return ProductViewHolder(itemVeiw, context, fragment)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductViewHolder) {
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

    private inner class ProductViewHolder(itemView: View, var context: Context?, fragment: ProductListFragment?) : RecyclerView.ViewHolder(itemView), RecyclerViewItemClickListener {
        var textBrand: CustomTextView
        var textName: CustomTextView
        var textMRP: CustomTextView
//        var textPrice1: CustomTextView

        //        var textPrice2: CustomTextView
        var textCount: CustomTextView
        var textOfferPrice: CustomTextView
        var textQty: CustomTextView
        var textQty2: CustomTextView
        var tvRating: CustomTextView
        var btnAdd: CustomBtn
        var clCount: ConstraintLayout
        var imgPlus: ImageView
        var imgMinus: ImageView
//        var layoutQty: RelativeLayout
        var fragment: ProductListFragment? = null
        var img: MyCartImageView
        var customDialog: CustomListViewDialog? = null
        fun clickHere(view: View?, list: ArrayList<ProductListParser.ProductList.UnitDetail?>?, productName: String?, position: Int) {
            val dataAdapter = UnitTypeAdapter(list, this, position)
            customDialog = CustomListViewDialog(context as Activity?, dataAdapter, productName)
            customDialog!!.show()
            customDialog!!.setCanceledOnTouchOutside(false)
        }

        fun bindView(product: ProductListParser.ProductList?, position: Int) {
            textBrand.text = product?.brand?.capitalize()
            textName.text = product?.productName?.capitalize()
//            tvRating.text = product?.


            if (product!!.qty == 0) {
                btnAdd.visibility = View.VISIBLE
                clCount.visibility = View.GONE
            } else {
                btnAdd.visibility = View.GONE
                clCount.visibility = View.VISIBLE
                textCount.text = product.qty.toString() + ""
            }
            if (!product.unitDetails?.isEmpty()!!) {
                val unitDetail = product.unitDetails?.get(0)
                if (unitDetail?.offerPrice != "0.0" && unitDetail?.offerPrice != "") {

                    /*textMRP.setTextColor(ContextCompat.getColor(context!!, R.color.light_grey))
                    textPrice1.setTextColor(ContextCompat.getColor(context!!, R.color.light_grey))
                    textPrice1.text = unitDetail?.unitPrice
                    textOfferPrice.setTextColor(ContextCompat.getColor(context!!, R.color.text_dark))

                    textPrice2.setTextColor(ContextCompat.getColor(context!!, R.color.dark_green))
                    textPrice2.text = unitDetail?.offerPrice*/

                    val mrp = unitDetail?.unitPrice?.let { getPriceString("MRP", it, ContextCompat.getColor(context!!, R.color.light_grey), ContextCompat.getColor(context!!, R.color.light_grey)) }

                    textMRP.setText(mrp, BufferType.SPANNABLE)
                    textMRP.paintFlags = textMRP.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


                    val offer = unitDetail?.offerPrice?.let { getPriceString("Offer Price", it, ContextCompat.getColor(context!!, R.color.splash_blue), ContextCompat.getColor(context!!, R.color.home_green)) }

                    textOfferPrice.setText(offer, BufferType.SPANNABLE)


                } else {
                   /* textMRP.setTextColor(ContextCompat.getColor(context!!, R.color.text_dark))
                    textPrice1.setTextColor(ContextCompat.getColor(context!!, R.color.dark_green))
                    textPrice1.text = unitDetail.unitPrice */
                    val mrp = unitDetail.unitPrice?.let { getPriceString("MRP", it, ContextCompat.getColor(context!!, R.color.splash_blue), ContextCompat.getColor(context!!, R.color.home_green)) }

                    textMRP.setText(mrp, BufferType.SPANNABLE)

                    textOfferPrice.visibility = View.GONE

//                    textPrice2.visibility = View.GONE
                }
            }
            if (!product.prodImages?.isEmpty()!!) img.loadImageWithUrl(product.prodImages?.get(0)?.imgUrl, R.drawable.image_placeholder, null)

            if (product.unitDetails?.size!! > 1) {
                textQty2.visibility = View.VISIBLE
                textQty.setText(View.GONE)
                textQty2.text = product.unitDetails?.get(0)?.unitType
            } else {
                textQty2.visibility = View.GONE
                textQty.visibility = View.VISIBLE
                if (!product.unitDetails!!.isEmpty()) {
                    textQty.text = product.unitDetails?.get(0)?.unitType
                }
            }

            textQty2.setOnClickListener { view -> clickHere(view, product.unitDetails as ArrayList<ProductListParser.ProductList.UnitDetail?>, product.brand + "-" + product.productName, position) }

            btnAdd.setOnClickListener {
                btnAdd.visibility = View.GONE
                clCount.visibility = View.VISIBLE
                addQty(position)
                if (fragment != null) {
                    fragment!!.calculateAmt()
                }
            }
            imgPlus.setOnClickListener {
                addQty(position)
                if (fragment != null) {
                    fragment!!.calculateAmt()
                }
            }
            imgMinus.setOnClickListener {
                deleteQty(position)
                if (fragment != null) {
                    fragment!!.calculateAmt()
                }
            }
            if (TextUtils.isNullOrEmpty(productList!![position]!!.seletecdUnitType)) {
                productList!![position]!!.selectedUnitPos = 0
                if (productList!![position]?.unitDetails?.isNotEmpty()!!) {
                    productList!![position]!!.seletecdUnitId = productList!![position]?.unitDetails?.get(0)?.id.toString()
                    productList!![position]!!.seletecdUnitType = productList!![position]?.unitDetails?.get(0)?.unitType.toString()
                }
            } else {
//                holder.txtAmount.setText("Rs "+getUnitPrice(productList.get(position).getUnitDetails().get(productList.get(position).selectedUnitPos)));
                productList!![position]!!.seletecdUnitId = productList!![position]?.unitDetails?.get(productList!![position]!!.selectedUnitPos)?.id.toString()
                productList!![position]!!.seletecdUnitType = productList!![position]?.unitDetails?.get(productList!![position]!!.selectedUnitPos)?.unitType.toString()
            }
        }

        override fun clickOnItem(data: ProductListParser.ProductList.UnitDetail?, position: Int, unitPos: Int) {
            if (customDialog != null) {
                customDialog!!.dismiss()
                textQty2.text = data?.unitType
                if (data?.offerPrice != "0.0" && data?.offerPrice != "") {
                    /*textMRP.setTextColor(ContextCompat.getColor(context!!, R.color.light_grey))
                    textPrice1.setTextColor(ContextCompat.getColor(context!!, R.color.light_grey))
                    textPrice1.text = data?.unitPrice*/
                    val mrp = data?.unitPrice?.let { getPriceString("MRP", it, ContextCompat.getColor(context!!, R.color.light_grey), ContextCompat.getColor(context!!, R.color.light_grey)) }

                    textMRP.setText(mrp, BufferType.SPANNABLE)
                    textMRP.paintFlags = textMRP.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


                    val offer = data?.offerPrice?.let { getPriceString("Offer Price", it, ContextCompat.getColor(context!!, R.color.splash_blue), ContextCompat.getColor(context!!, R.color.home_green)) }

                    textOfferPrice.setText(offer, BufferType.SPANNABLE)

                    /*textOfferPrice.setTextColor(ContextCompat.getColor(context!!, R.color.text_dark))
                    textPrice2.setTextColor(ContextCompat.getColor(context!!, R.color.dark_green))
                    textPrice2.text = data?.offerPrice*/

                } else {
                   /* textMRP.setTextColor(ContextCompat.getColor(context!!, R.color.text_dark))
                    textPrice1.setTextColor(ContextCompat.getColor(context!!, R.color.dark_green))
                    textPrice1.text = data.unitPrice
*/
                    val mrp = data.unitPrice?.let { getPriceString("MRP", it, ContextCompat.getColor(context!!, R.color.splash_blue), ContextCompat.getColor(context!!, R.color.home_green)) }

                    textMRP.setText(mrp, BufferType.SPANNABLE)



                    textOfferPrice.visibility = View.GONE
//                    textPrice2.visibility = View.GONE
                }
                productList!![position]!!.selectedUnitPos = unitPos
                productList!![position]!!.seletecdUnitId = productList!![position]?.unitDetails?.get(unitPos)?.id.toString()
                productList!![position]!!.seletecdUnitType = productList!![position]?.unitDetails?.get(unitPos)?.unitType.toString()
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
                        clCount.visibility = View.GONE
                        btnAdd.visibility = View.VISIBLE
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
                    break
                }
            }
        }

        init {
            textBrand = itemView.findViewById(R.id.textBrand)
            textName = itemView.findViewById(R.id.textName)
            textCount = itemView.findViewById(R.id.textCount)
            textMRP = itemView.findViewById(R.id.textMRP)
            textOfferPrice = itemView.findViewById(R.id.textOfferPrice)
//            textPrice1 = itemView.findViewById(R.id.textPrice)
//            textPrice2 = itemView.findViewById(R.id.textPrice2)
            textQty = itemView.findViewById(R.id.textQty)
            textQty2 = itemView.findViewById(R.id.textQty2)
//            layoutQty = itemView.findViewById(R.id.ll_quantity)
            img = itemView.findViewById(R.id.itemImage)
            btnAdd = itemView.findViewById(R.id.btn_add)
            clCount = itemView.findViewById(R.id.cl_count)
            imgPlus = itemView.findViewById(R.id.imgPlus)
            imgMinus = itemView.findViewById(R.id.imgMinus)
            tvRating = itemView.findViewById(R.id.textRating)
            this.fragment = fragment
        }
    }


    private fun getPriceString(title: String, value: String, titleColor: Int, valueColor: Int): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        val a = ContextCompat.getColor(context!!, R.color.light_grey)

        val titleString = title.toUpperCase() + ": "
        val redSpannable = SpannableString(titleString)
        redSpannable.setSpan(ForegroundColorSpan(titleColor), 0, titleString.length, 0)
        builder.append(redSpannable)

        val valueString = value
        val whiteSpannable = SpannableString(valueString)
        whiteSpannable.setSpan(ForegroundColorSpan(valueColor), 0, valueString.length, 0)
        builder.append(whiteSpannable)

        return builder
    }


}
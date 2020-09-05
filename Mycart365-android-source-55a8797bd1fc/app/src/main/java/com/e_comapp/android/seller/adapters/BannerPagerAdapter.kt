package com.e_comapp.android.seller.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.e_comapp.android.R
import com.e_comapp.android.views.CustomTextView
import com.facebook.drawee.view.SimpleDraweeView

class BannerPagerAdapter : PagerAdapter {
    var mContext: Context
    var imageUrls: List<String>? = null
    var isStatic = false
    var resourceList: List<Int>? = null

    constructor(mContext: Context, imageUrls: List<String>?) {
        this.mContext = mContext
        this.imageUrls = imageUrls
    }

    constructor(mContext: Context, isStatic: Boolean, resourceList: List<Int>?) {
        this.mContext = mContext
        this.isStatic = isStatic
        this.resourceList = resourceList
    }

    override fun getCount(): Int {
        return if (isStatic) {
            resourceList!!.size
        } else {
            imageUrls!!.size
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return if (isStatic) {
            val view = (mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.layout_otp_image, null)
            (view.findViewById<View>(R.id.image) as ImageView).setImageResource(resourceList!![position])
            if (position == 0) {
                (view.findViewById<View>(R.id.text1) as CustomTextView).text = mContext.getString(R.string.str_no)
                (view.findViewById<View>(R.id.text2) as CustomTextView).text = mContext.getString(R.string.str_minimum_order)
            } else if (position == 1) {
                (view.findViewById<View>(R.id.text1) as CustomTextView).text = mContext.getString(R.string.str_fast)
                (view.findViewById<View>(R.id.text2) as CustomTextView).text = mContext.getString(R.string.str_safe_delivery)
            }
            (container as ViewPager).addView(view, 0)
            view
        } else {
            val view = (mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.image_layout, null)
            (view.findViewById<View>(R.id.image) as SimpleDraweeView).setImageURI(imageUrls!![position])
            (container as ViewPager).addView(view, 0)
            view
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        (container as ViewPager).removeView(`object` as View)
    }
}
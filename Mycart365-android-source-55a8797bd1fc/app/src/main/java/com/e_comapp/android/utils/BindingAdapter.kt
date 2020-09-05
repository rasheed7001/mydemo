package com.e_comapp.android.utils

import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.e_comapp.android.R
import com.e_comapp.android.util.component.CircleImageView
import com.e_comapp.android.util.component.MyCartImageView

/**
 * Binding adapter class for binding the adapter image
 * */
object BindingAdapter {

    /**
     * to set the rating for the rating view
     * */
    @JvmStatic
    @BindingAdapter("rating")
    fun setRating(view: RatingBar?, rating: String?) {
        if (view != null && rating != null) {
            val rate = rating.toFloat()
            view.rating = rate
        }
    }

    /**
     * To show image from url
     * @param imageView
     * @param imageUrl
     * @param placeholder
     */
    @JvmStatic
    @BindingAdapter(value = ["android:src", "placeholder"], requireAll = false)
    fun setImageUrl(view: ImageView, profileUrl: String?, placeHolder: Drawable?) {
        Glide.with(view.context)
            .load(profileUrl)
            .placeholder(placeHolder)
            .error(placeHolder)
            .into(view)
    }

    /**
     * To show image from drawable
     *
     * @param imageView
     * @param drawableId
     * @param placeholder
     */
    @JvmStatic
    @BindingAdapter(value = ["drawableId", "placeholder"], requireAll = false)
    fun setDrawableImage(view: ImageView, drawableId: Int, placeHolder: Drawable?) {
        Glide.with(view.context)
            .load(if (drawableId != 0) drawableId else null)
            .placeholder(placeHolder)
            .error(placeHolder)
            .into(view)
    }

   /* *//**
     * Sets the view as visible if true otherwise as invisible
     *//*
    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(view: MyCartImageView, documentDetails: DocumentDataDetails?) {
        if (documentDetails != null && !TextUtils.isEmpty(documentDetails.url)) {
            documentDetails.url?.let { view.loadImageWithUrl(it) }
        } else if (documentDetails?.uri != null) {
            documentDetails.uri?.let { view.loadImageWithUri(it) }
        }
    }

    *//**
     * Sets the view as visible if true otherwise as invisible
     *//*
    @JvmStatic
    @BindingAdapter("loadCircularImage")
    fun loadImage(view: CircleImageView, documentDetails: DocumentDataDetails?) {
        if (documentDetails != null && TextUtils.isEmpty(documentDetails.uri?.toString())) {
            documentDetails.url?.let {
                view.loadImageWithRelativeUrl(
                    it,
                    R.drawable.ic_account_circle_black_24dp,
                    null
                )
            }
        } else {
            documentDetails?.uri?.let {
                view.loadImageWithRelativeUri(
                    it,
                    R.drawable.ic_account_circle_black_24dp,
                    null
                )
            }
        }
    }*/

    /**
     * Sets the view as visible if true otherwise as invisible
     */
    @JvmStatic
    @BindingAdapter("loadCircularImageWithUrl")
    fun loadImage(view: CircleImageView, url: String?) {
        url?.let {
            view.loadImageWithRelativeUrl(
                it,
                R.drawable.seller_profile_s,
                null
            )
        }
    }

    /**
     * Sets the view as visible if true otherwise as invisible
     */
    @JvmStatic
    @BindingAdapter("loadCircularImageWithUri")
    fun loadImage(view: CircleImageView, uri: Uri?) {
        uri?.let {
            view.loadImageWithRelativeUri(
                    it,
                    R.drawable.seller_profile_s,
                    null
            )
        }
    }

    /**
     * Sets the view as visible if true otherwise as invisible
     */
    @JvmStatic
    @BindingAdapter("setTextWithoutNull")
    fun setText(view: AppCompatTextView, url: String?) {
        if (url == null) {
            view.text = ""
        } else {
            view.text = url
        }
    }

    /**
     * Sets the view as visible if true otherwise as invisible
     */
    @JvmStatic
    @BindingAdapter("visibleOrGone")
    fun visibleOrGone(view: View, visible: Boolean) {
        view.visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

}

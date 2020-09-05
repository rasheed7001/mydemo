package com.e_comapp.android.util.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.e_comapp.android.R

/**
 * Subclass of [android.widget.ImageView] with the added functionality of
 * seamlessly loading images from the internet.
 *
 */
open class MyCartImageView : AppCompatImageView {
    /**
     * Instantiates a new image view.
     *
     * @param context the context
     */
    constructor(context: Context?) : super(context!!)

    /**
     * Instantiates a new image view.
     *
     * @param context the context
     * @param attrs the attrs
     */
    constructor(context: Context?, attrs: AttributeSet?) : super(
            context!!,
        attrs
    )

    /**
     * Instantiates a new image view.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context!!, attrs, defStyle)

    /**
     * Loads image from the network against the specified URL.
     *
     * @param imageUrl A valid URL
     */
    fun loadImageWithUrl(imageUrl: String?) {

        val cpdImageLoader = CircularProgressDrawable(context).apply {
            strokeWidth = STROKE_WITH
            centerRadius = CENTRE_RADIUS
            setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
            start()
        }
        if (TextUtils.isEmpty(imageUrl)) {
            return
        }
        Glide.with(context).load(imageUrl).placeholder(cpdImageLoader)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    cpdImageLoader.stop()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    cpdImageLoader.stop()
                    return false
                }
            }).into(this)
    }

    /**
     * Used to set the load the circular image with drawable res id
     * */
    fun loadImageWithUri(resId: Uri) {

        val cpdImageLoader = CircularProgressDrawable(context).apply {
            strokeWidth = STROKE_WITH
            centerRadius = CENTRE_RADIUS
            setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
            start()
        }

        Glide.with(context).load(resId).placeholder(cpdImageLoader)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    cpdImageLoader.stop()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    cpdImageLoader.stop()
                    return false
                }
            }).into(this)
    }

    /**
     * Used to load the image with url in glide
     * */
    @JvmOverloads
    fun loadImageWithUrl(
        imageUrl: String?,
        defaultResourceID: Int,
        imageLoadListener: ImageLoadListener? = null
    ) {
        if (TextUtils.isEmpty(imageUrl)) {
            setImageResource(defaultResourceID)
            return
        }
        Glide.with(context).load(imageUrl).apply(getCustomReqeustOptions(defaultResourceID))
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    imageLoadListener?.onImageLoadCompleted(false)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    imageLoadListener?.onImageLoadCompleted(true)
                    return false
                }
            }).into(this)
    }

    /**
     * Used to set the load the circular image with drawable res id
     * */
    fun setCircularImageResource(resId: Int) {
        Glide.with(context).load(resId).apply(RequestOptions.circleCropTransform()).into(this)
    }

    private fun getCustomReqeustOptions(defaultResourceID: Int): RequestOptions {
        return RequestOptions().centerCrop().placeholder(defaultResourceID).error(defaultResourceID)
            .diskCacheStrategy(DiskCacheStrategy.ALL).priority(Priority.HIGH)
            .dontAnimate().dontTransform()
    }
}
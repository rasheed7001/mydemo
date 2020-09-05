package com.e_comapp.android.util.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
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
import java.io.ByteArrayOutputStream

private const val HUNDRED = 100

/**
 * Created on 7/6/2018.
 */
class CircleImageView : MyCartImageView {
    private var strokeWidth = 0
    private var strokeColor = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        setupAttributes(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        if (isInEditMode) return
        val a = this.context.theme
            .obtainStyledAttributes(attrs, R.styleable.CircleImageViewMyCart, 0, 0)
        try {
            strokeWidth = a.getDimensionPixelSize(R.styleable.CircleImageViewMyCart_strokeWidth, 0)
            strokeColor = a.getColor(
                R.styleable.CircleImageViewMyCart_strokeColor,
                Color.TRANSPARENT
            )
            setStroke(strokeWidth, strokeColor)
            val drawable = a.getDrawable(R.styleable.CircleImageViewMyCart_android_src)
            drawable?.let { loadDefaultImage(it) }
        } finally {
            a.recycle()
        }
    }

    /**
     * load image with url
     * */
    fun loadImageWithRelativeUrl(imageUrl: String?, defaultResourceID: Int) {
        if (TextUtils.isEmpty(imageUrl)) {
            setCircularImageResource(defaultResourceID)
            return
        }
        loadImage(imageUrl, defaultResourceID, null)
    }

    /**
     * Load the image with url and if error it will show empty place holder
     * */
    fun loadImageWithRelativeUrl(
        imageUrl: String?,
        defaultPlaceholder: Int,
        emptyPlaceholder: Int
    ) {
        if (TextUtils.isEmpty(imageUrl)) {
            setCircularImageResource(emptyPlaceholder)
            return
        }
        loadImage(imageUrl, defaultPlaceholder, null)
    }

    /**
     * Load image with url
     * */
    fun loadImageWithRelativeUrl(
        imageUrl: String?,
        defaultPlaceholder: Int,
        emptyPlaceholder: Int,
        imageLoadListener: ImageLoadListener?
    ) {
        if (TextUtils.isEmpty(imageUrl)) {
            setCircularImageResource(emptyPlaceholder)
            return
        }
        loadImage(imageUrl, defaultPlaceholder, imageLoadListener)
    }

    /**
     * Load image with the url
     * */
    fun loadImageWithRelativeUrl(
        imageUrl: String?,
        defaultResourceID: Int,
        imageLoadListener: ImageLoadListener?
    ) {
        if (TextUtils.isEmpty(imageUrl)) {
            setCircularImageResource(defaultResourceID)
            return
        }
        loadImage(imageUrl, defaultResourceID, imageLoadListener)
    }

    /**
     * Load image with the url
     * */
    fun loadImageWithRelativeUri(
        imageUri: Uri?,
        defaultResourceID: Int,
        imageLoadListener: ImageLoadListener?
    ) {
        if (imageUri == null) {
            setCircularImageResource(defaultResourceID)
            return
        }
        loadImageWithUri(imageUri, defaultResourceID, imageLoadListener)
    }

    private fun loadImage(
        imageUrl: String?,
        defaultResourceID: Int,
        imageLoadListener: ImageLoadListener?
    ) {
        val cpdImageLoader = CircularProgressDrawable(context).apply {
            strokeWidth = STROKE_WITH
            centerRadius = CENTRE_RADIUS
            setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
            start()
        }

        val options =
            RequestOptions().centerCrop().placeholder(cpdImageLoader).error(defaultResourceID)
                .diskCacheStrategy(DiskCacheStrategy.ALL).priority(Priority.HIGH)
                .dontAnimate().dontTransform()
        Glide.with(context).load(imageUrl).apply(options)
            .apply(RequestOptions.circleCropTransform())
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }).into(this)
    }

    private fun loadImageWithUri(
        imageUri: Uri?,
        defaultResourceID: Int,
        imageLoadListener: ImageLoadListener?
    ) {
        val cpdImageLoader = CircularProgressDrawable(context).apply {
            strokeWidth = STROKE_WITH
            centerRadius = CENTRE_RADIUS
            setColorSchemeColors(ContextCompat.getColor(context, R.color.colorPrimary))
            start()
        }

        val options =
            RequestOptions().centerCrop().placeholder(cpdImageLoader).error(defaultResourceID)
                .diskCacheStrategy(DiskCacheStrategy.ALL).priority(Priority.HIGH)
                .dontAnimate().dontTransform()
        Glide.with(context).load(imageUri).apply(options)
            .apply(RequestOptions.circleCropTransform())
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }).into(this)
    }

    private fun loadDefaultImage(
        defaultResourceID: Drawable
    ) {
        val options =
            RequestOptions().centerCrop().placeholder(R.drawable.ic_seller)
                .error(R.drawable.ic_seller)
                .diskCacheStrategy(DiskCacheStrategy.ALL).priority(Priority.HIGH)
                .dontAnimate().dontTransform()
        Glide.with(context).load(defaultResourceID).apply(options)
            .apply(RequestOptions.circleCropTransform())
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }).into(this)
    }

    /**
     * Used to set the stroke width for the circular image view
     * */
    fun setStrokeWidth(strokeWidth: Int) {
        this.strokeWidth = strokeWidth
        setImageDrawable(drawable)
    }

    /**
     * Used to set stroke color for the image view
     * */
    fun setStrokeColor(strokeColor: Int) {
        this.strokeColor = strokeColor
        setImageDrawable(drawable)
    }

    /**
     * Used to set stroke width and color
     * */
    fun setStroke(strokeWidth: Int, strokeColor: Int) {
        this.strokeWidth = strokeWidth
        this.strokeColor = strokeColor
        setImageDrawable(drawable)
    }

    override fun setImageBitmap(bm: Bitmap) {
        val stream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, HUNDRED, stream)
        val options =
            RequestOptions().centerCrop().apply(RequestOptions().circleCrop())
                .diskCacheStrategy(DiskCacheStrategy.ALL).priority(Priority.HIGH)
                .dontAnimate().dontTransform()
        Glide.with(context).asBitmap().load(stream.toByteArray()).apply(options).into(this)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (isInEditMode) {
            super.setImageDrawable(drawable)
            return
        }
        super.setImageDrawable(drawable)
    }

    /**
     * This will load image with the url, otherwise it will show default resource id
     * */
    fun loadImageWithRelativeUrl(
        imageUrl: String?,
        defaultResourceID: Int,
        isVectorDrawable: Boolean
    ) {
        if (TextUtils.isEmpty(imageUrl)) {
            setVectorDrawable(defaultResourceID, isVectorDrawable)
            return
        }
        loadImage(imageUrl, defaultResourceID, null)
    }

    private fun setVectorDrawable(
        defaultResourceID: Int,
        isVectorDrawable: Boolean
    ) {
        if (isVectorDrawable) setImageResource(defaultResourceID) else setCircularImageResource(
            defaultResourceID
        )
    }

    /**
     * Used to load image with url
     * */
    fun loadImageWithRelativeUrl(
        imageUrl: String?,
        defaultPlaceholder: Int,
        emptyPlaceholder: Int,
        isVectorDrawable: Boolean
    ) {
        if (TextUtils.isEmpty(imageUrl)) {
            setVectorDrawable(emptyPlaceholder, isVectorDrawable)
            return
        }
        loadImage(imageUrl, defaultPlaceholder, null)
    }
}
package com.e_comapp.android.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatEditText
import com.e_comapp.android.R
import com.e_comapp.android.utils.TextUtils

class CustomEditText : AppCompatEditText {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        applyTypeFace(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        applyTypeFace(context, attrs, defStyleAttr)
    }

    private fun applyTypeFace(context: Context, attrs: AttributeSet?, defStyle: Int) {
        try {
            val a = getContext().obtainStyledAttributes(attrs, R.styleable.font)
            var str = a.getString(R.styleable.font_fontType)
            Log.e("Typeface", "" + str)
            if (TextUtils.isNullOrEmpty(str)) {
                str = "0"
            }
            typeface = TypeFaceUtils.getTypeFace(context, str.toInt())
            a.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
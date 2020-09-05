package com.e_comapp.android.views

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import java.util.*

class FontManager private constructor(private val mContext: Context) {
    private val fontCache: MutableMap<String, Typeface> = HashMap()
    fun loadFont(font: String): Typeface? {
        if (!fontCache.containsKey(font)) {
            fontCache[font] = Typeface.createFromAsset(mContext.assets, font)
            Log.e("Font", "Type Loading")
        }
        return fontCache[font]
    }

    companion object {
        private var instance: FontManager? = null

        @Synchronized
        fun getInstance(mContext: Context): FontManager? {
            if (instance == null) {
                instance = FontManager(mContext)
            }
            return instance
        }
    }

}
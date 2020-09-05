package com.e_comapp.android.views

import android.content.Context
import android.graphics.Typeface

object TypeFaceUtils {
    fun getTypeFace(context: Context, font: Int): Typeface? {
        val str: String
        str = when (font) {
            0 -> "font/segoe_ui.ttf"
            1 -> "font/segoe_ui_bold.ttf"
            2 -> "font/segoe_ui_bold_italic.ttf"
            3 -> "font/segoe_ui_italic.ttf"
            4 -> "font/segoe_ui_semibold.ttf"
            else -> "font/segoe_ui.ttf"
        }
        return FontManager.Companion.getInstance(context)!!.loadFont(str)
    }
}
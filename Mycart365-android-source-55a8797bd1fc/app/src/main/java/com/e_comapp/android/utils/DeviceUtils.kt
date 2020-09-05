package com.e_comapp.android.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.io.IOException
import java.util.*

object DeviceUtils {
    fun autoLaunchVivo(context: Context) {
        initOPPO(context)
        try {
            val intent = Intent()
            intent.component = ComponentName("com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val intent = Intent()
                intent.component = ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")
                context.startActivity(intent)
            } catch (ex: Exception) {
                try {
                    val intent = Intent()
                    intent.setClassName("com.iqoo.secure",
                            "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")
                    context.startActivity(intent)
                } catch (exx: Exception) {
                    ex.printStackTrace()
                }
            }
        }
        if (Build.MANUFACTURER.equals("oppo", ignoreCase = true)) {
            try {
                val intent = Intent()
                intent.setClassName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity")
                context.startActivity(intent)
            } catch (e: Exception) {
                try {
                    val intent = Intent()
                    intent.setClassName("com.oppo.safe",
                            "com.oppo.safe.permission.startup.StartupAppListActivity")
                    context.startActivity(intent)
                } catch (ex: Exception) {
                    try {
                        val intent = Intent()
                        intent.setClassName("com.coloros.safecenter",
                                "com.coloros.safecenter.startupapp.StartupAppListActivity")
                        context.startActivity(intent)
                    } catch (exx: Exception) {
                    }
                }
            }
        }
    }

    private fun initOPPO(context: Context) {
        try {
            val i = Intent(Intent.ACTION_MAIN)
            i.component = ComponentName("com.oppo.safe", "com.oppo.safe.permission.floatwindow.FloatWindowListActivity")
            context.startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val intent = Intent("action.coloros.safecenter.FloatWindowListActivity")
                intent.component = ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.floatwindow.FloatWindowListActivity")
                context.startActivity(intent)
            } catch (ee: Exception) {
                ee.printStackTrace()
                try {
                    val i = Intent("com.coloros.safecenter")
                    i.component = ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity")
                    context.startActivity(i)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }
        }
    }

    fun getDeviceId(context: Context): String {
        return "" + Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun setSystemUiVisibility(view: View) {
        if (Build.VERSION.SDK_INT >= 21) {
            view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

    fun showSoftKeyboard(context: Context, paramView: View?) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(paramView,
                InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideSoftKeyboard(context: Context, paramView: View) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                paramView.windowToken, 0)
    }

    fun hideSoftKeyboard(activity: Activity?) {
        val view = activity!!.currentFocus
        if (view != null) {
            val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun isInternetConnected(context: Context?): Boolean {
        val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        var isConnected = false
        if (info != null && info.isConnectedOrConnecting) {
            isConnected = true
        }
        return isConnected
    }

    fun setStatusBarColor(context: Context, colorId: Int) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val window = (context as Activity).window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(context, colorId)
        }
    }

    val phoneLocale: String
        get() = Locale.getDefault().toString()

    val phoneLocaleLanguage: String
        get() = Locale.getDefault().toString().substring(0, Locale.getDefault().toString().lastIndexOf("_"))

    fun getPixelFromDp(context: Context, dpUnits: Int): Int {
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpUnits.toFloat(), getDisplayMetrics(context))
        return px.toInt()
    }

    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val outMetrics = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(outMetrics)
        return outMetrics
    }

    @Throws(IOException::class)
    fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap {
        val ei = ExifInterface(selectedImage.path)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(img, 270)
            else -> img
        }
    }

    fun scaleBitmap(bitmap: Bitmap, wantedWidth: Int, wantedHeight: Int): Bitmap {
        val output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val m = Matrix()
        m.setScale(wantedWidth.toFloat() / bitmap.width, wantedHeight.toFloat() / bitmap.height)
        canvas.drawBitmap(bitmap, m, Paint())
        return output
    }
}
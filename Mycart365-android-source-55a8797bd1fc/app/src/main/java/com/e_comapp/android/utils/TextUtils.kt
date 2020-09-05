package com.e_comapp.android.utils

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Build.VERSION_CODES
import android.text.*
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Base64
import android.util.Patterns
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

object TextUtils {
    private const val REGX_HASHTAG = ("[`@!\\&×\\÷~#\\-\\+=\\[\\]{}\\^()<>/;:,.?'|\"\\*%$\\s+\\\\"
            + "•??£¢€°™®©¶¥??????????¿¡??¤??]" // #$%^*()+=\-\[\]\';,.\/{}|":<>?~\\\\
            )

    private const val REGEX_FILTER = ("(([1-9]{1})([0-9]{0,4})?(\\.)?)?([0-9]{0,2})?")

    var PATTERN_HASHTAG: Pattern? = null

    //    public static String html2text(String html) {
    //        return Jsoup.parse(html).text();
    //    }
    fun stripHtml(html: String): String {
//        html=html2text(html);
        var html = html
        html = html.replace("\\<.*?\\>".toRegex(), "")
        return if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(html).toString()
        }
    }

    /*fun isStringOnly(str: String?): Boolean {
        val regx = "^[\\p{L} .'-]+$"
        return (str != ""
                && str != null
                && str.matches(regx))
    }*/

    fun encodeToBase64(content: CharSequence?): String? {
        if (content == null) {
            return null
        }
        val bytes = Base64.encode(content.toString().toByteArray(), Base64.DEFAULT)
        return String(bytes).trim { it <= ' ' }
    }

    fun encodeToBase64(data: ByteArray?): String {
        val bytes = Base64.encode(data, Base64.DEFAULT)
        return String(bytes).trim { it <= ' ' }
    }

    fun decodeBase64(base64String: String?): String? {
        if (base64String == null) {
            return base64String
        }
        try {
            return String(Base64.decode(base64String, Base64.DEFAULT))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return base64String
    }

    fun isEmpty(value: String?): Boolean {
        return value == null || value.trim { it <= ' ' } == "" || value.trim { it <= ' ' } == "null"
    }

    fun isEmpty(value: CharSequence?): Boolean {
        return value == null || value.toString() == "" || value.toString() == "null"
    }

    fun isEmpty(inputLayout: TextInputLayout?): Boolean {
        return if (inputLayout != null) {
            isEmpty(inputLayout.editText)
        } else true
    }

    fun isEmpty(editText: EditText?): Boolean {
        return if (editText != null) {
            isEmpty(editText.text.toString())
        } else true
    }

    fun isEmpty(textView: TextView?): Boolean {
        return if (textView != null) {
            isEmpty(textView.text)
        } else true
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun isValidWebUrl(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            Patterns.WEB_URL.matcher(target).matches()
        }
    }

    fun isValidHost(target: CharSequence?): Boolean {
        try {
            val uri = URI(target as String?)
            if (uri.host != null) {
                return true
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        return false

        /* if (target == null) {
            return false;
        } else {
            return Patterns.DOMAIN_NAME.matcher(target).;
        }*/
    }

    fun truncate(value: String?, length: Int): String? {
        var value = value
        if (value != null && value.length > length) {
            value = value.substring(0, length)
            value += "..."
        }
        return value
    }

    fun isNumber(text: String): Boolean {
        try {
            text.toInt()
            return true
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return false
    }

    fun isJsonData(content: String?): Boolean {
        return content != null && content.startsWith("{") && content.endsWith("}")
    }

    fun asUpperCaseFirstChar(target: String?): String? {
        return if (target == null || target.length == 0) {
            target // You could omit this check and simply live with an
            // exception if you like
        } else Character.toUpperCase(target[0])
                .toString() + if (target.length > 1) target.substring(1) else ""
    }

    fun isNullOrEmpty(value: String?): Boolean {
        return value == null || value.trim { it <= ' ' } == "" || value.equals("null", ignoreCase = true)
    }

    fun isNullOrEmpty(value: CharSequence?): Boolean {
        return value == null || value.toString() == ""
    }

    fun arrayToString(array: ArrayList<String?>, delimiter: String?): String {
        val builder = StringBuilder()
        if (array.size > 0) {
            builder.append(array[0])
            for (i in 1 until array.size) {
                builder.append(delimiter)
                builder.append(array[i])
            }
        }
        return builder.toString()
    }

    fun stringToArray(string: String): ArrayList<String> {
        return ArrayList(Arrays.asList(*string.split(",".toRegex()).toTypedArray()))
    }

    fun integerArrayToString(array: ArrayList<Int?>, delimiter: String?): String {
        val builder = StringBuilder()
        if (array.size > 0) {
            builder.append(array[0])
            for (i in 1 until array.size) {
                builder.append(delimiter)
                builder.append(array[i])
            }
        }
        return builder.toString()
    }

    fun capitalizeFirstLetter(original: String): String {
        return if (original.length == 0) original else original.substring(0, 1).toUpperCase() + original.substring(1)
    }

    fun getOnlyDigits(s: String?): String {
        val pattern = Pattern.compile("[^0-9]")
        val matcher = pattern.matcher(s)
        return matcher.replaceAll("")
    }

    fun round(value: Double, places: Int): Double {
        var value = value
        require(places >= 0)
        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value = value * factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }

    fun fromHtml(string: String?): String {
        val result: Spanned
        result = if (Build.VERSION.SDK_INT >= VERSION_CODES.N) {
            Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(string)
        }
        return result.toString()
    }

    fun doSizeSpanForFirstString(firstString: String?,
                                 lastString: String, color: Int): Spannable {
        val changeString = firstString ?: ""
        val totalString = changeString + lastString
        val spanText: Spannable = SpannableString(totalString)
        spanText.setSpan(ForegroundColorSpan(color), 0, changeString.length, 0)
        spanText.setSpan(RelativeSizeSpan(1.5f), 0, changeString.length, 0)
        return spanText
    }

    fun doSizeSpanForSecondString(firstString: String?,
                                  lastString: String, firstColor: Int, secondColor: Int): Spannable {
        val changeString = firstString ?: ""
        val totalString = changeString + lastString
        val spanText: Spannable = SpannableString(totalString)
        spanText.setSpan(ForegroundColorSpan(firstColor), 0, changeString.length, 0)
        spanText.setSpan(RelativeSizeSpan(1.5f), 0, changeString.length, 0)
        spanText.setSpan(ForegroundColorSpan(secondColor), firstString.toString().length, totalString.length, 0)
        spanText.setSpan(RelativeSizeSpan(1.0f), firstString.toString().length, totalString.length, 0)
        return spanText
    }

    fun reduceMarginsInTabs(tabLayout: TabLayout, marginOffset: Int) {
        val tabStrip = tabLayout.getChildAt(0)
        if (tabStrip is ViewGroup) {
            for (i in 0 until tabStrip.childCount) {
                val tabView = tabStrip.getChildAt(i)
                if (tabView.layoutParams is MarginLayoutParams) {
                    (tabView.layoutParams as MarginLayoutParams).leftMargin = marginOffset
                    (tabView.layoutParams as MarginLayoutParams).rightMargin = marginOffset
                }
            }
            tabLayout.requestLayout()
        }
    }

    val filter = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
        val builder = StringBuilder(dest)
        builder.replace(dstart, dend, source
                .subSequence(start, end).toString())

        val matcher: Matcher
        val pattern: Pattern = Pattern.compile(REGEX_FILTER)
        matcher = pattern.matcher(builder.toString())

        if (matcher.matches()) {
            return@InputFilter if (source.length == 0) dest.subSequence(dstart, dend) else ""
        } else {
            null
        }
        null
    }
    )

    fun checkNullValues(data: String?): String? {
        var returnVal: String? = ""
        if (!isNullOrEmpty(data)) {
            returnVal = data
        }
        return returnVal
    }

    fun withSuffix(count: Long): String {
        if (count < 1000) return "" + count
        val exp = (Math.log(count.toDouble()) / Math.log(1000.0)).toInt()
        val value = count / Math.pow(1000.0, exp.toDouble())
        return String.format("%d %c",
                value.toInt(),
                "kMGTPE"[exp - 1])
    }

    fun getSalaryRange(from: Int, to: Int, increment: Int): ArrayList<String> {
        val salaryRangeList = ArrayList<String>()
        var i = from
        while (i <= to) {
            salaryRangeList.add("" + i)
            i = i + increment
        }
        return salaryRangeList
    }

    fun getExpRange(from: Int, to: Int, increment: Int): ArrayList<String> {
        val salaryRangeList = ArrayList<String>()
        var i = from
        while (i <= to) {
            salaryRangeList.add("" + i)
            i = i + increment
        }
        return salaryRangeList
    }

    fun changeDrawableColor(context: Context, @ColorRes colorToId: Int,
                            @DrawableRes drawableToChangeId: Int): Drawable {
//        Log.v(TAG, "changeDrawableColor");
        val color = context.resources.getColor(colorToId)
        val drawable = context.resources.getDrawable(drawableToChangeId)
        drawable.setColorFilter(color, PorterDuff.Mode.OVERLAY)
        return drawable
    }

    init {
        PATTERN_HASHTAG = Pattern.compile(REGX_HASHTAG)
    }
}
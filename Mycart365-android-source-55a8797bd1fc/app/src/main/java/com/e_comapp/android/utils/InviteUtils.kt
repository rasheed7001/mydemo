package com.e_comapp.android.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.e_comapp.android.R

object InviteUtils {
    fun shareAppLink(context: Context, message: String) {
        var message = message
        val appPackageName = context.packageName
        message = "$message\nhttps://play.google.com/store/apps/details?id=$appPackageName"
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            i.putExtra(Intent.EXTRA_TEXT, message)
            context.startActivity(Intent.createChooser(i, "choose one"))
        } catch (e: Exception) {
            //e.toString();
        }
    }

    fun viewNewsLink(context: Context, link: String?) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        } catch (e: Exception) {
            //e.toString();
        }
    }

    fun openPlaystore(context: Context) {
        val appPackageName = context.packageName // getPackageName() from Context or Activity object
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }
}
package com.e_comapp.android.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.e_comapp.android.R

object AlertUtils {
    @JvmOverloads
    fun showAlert(context: Context?, title: String?, message: String?,
                  onClick: DialogInterface.OnClickListener? = null, cancelable: Boolean = false) {
        if (!(context as Activity?)!!.isFinishing) {
            AlertDialog.Builder(context!!, R.style.Theme_Alert)
                    .setMessage(message)
                    .setTitle(if (title != null && title != "") title else context.getString(R.string.app_name)) //TODO null check the title with TextUtils.
                    .setCancelable(cancelable)
                    .setPositiveButton(android.R.string.ok, onClick)
                    .create().show()
        }
    }

    /* public static void showCustomAlert(Context context, String title, String message, final DialogInterface.OnClickListener onClick, boolean cancelable ){
        if (!((Activity) context).isFinishing()) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View subView = inflater.inflate(R.layout.custom_alert_layout, null);
            TextView txtTitle = (TextView) subView.findViewById(R.id.txt_title);
            TextView txtMessage = (TextView) subView.findViewById(R.id.txt_message);
            ImageView imgClose = (ImageView) subView.findViewById(R.id.img_close);
            txtTitle.setText((title != null && !title.equals("")) ? title : context.getString(R.string.app_name));
            txtMessage.setText(message);

            final AlertDialog dialog=new AlertDialog.Builder(context)
                    .setView(subView)
                    .setCancelable(cancelable)
                    .create();
            dialog.show();
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.onClick(dialog,0);
                }
            });
        }
    }*/
    fun showNoInternetConnection(context: Context?) {
        showAlert(context, context!!.getString(R.string.app_name), "No Internet Connection", null, false)
    }

    fun getBuilder(context: Context?): AlertDialog.Builder {
        return AlertDialog.Builder(context!!, R.style.Dialog)
    }

    fun showBackAlert(context: Context, title: String?, message: String?) {
        if (!(context as Activity).isFinishing) {
            val builder = AlertDialog.Builder(context, R.style.Theme_Alert)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setNeutralButton("Ok") { dialogInterface, i -> context.onBackPressed() }
            builder.show()
        }
    }

    fun showAlert(context: Context?, message: String?) {
        showAlert(context, null, message, null, false)
    }

    fun showAlert(context: Context?, message: String?, onClick: DialogInterface.OnClickListener?, cancelable: Boolean) {
        showAlert(context, null, message, onClick, cancelable)
    }

    fun showToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun showAlert(context: Context, title: String?, message: String?,
                  okClick: DialogInterface.OnClickListener?, cancelClick: DialogInterface.OnClickListener?, cancelable: Boolean) {
        if (!(context as Activity).isFinishing) {
            AlertDialog.Builder(context, R.style.Theme_Alert)
                    .setMessage(message)
                    .setTitle(if (title != null && title != "") title else context.getString(R.string.app_name)) //TODO null check the title with TextUtils.
                    .setCancelable(cancelable)
                    .setPositiveButton(android.R.string.ok, okClick)
                    .setNegativeButton(android.R.string.cancel, cancelClick)
                    .create().show()
        }
    }
}
package com.e_comapp.android.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Extension function for afterTextChanged on EditText
 */
fun EditText.afterTextChanged(callback: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            callback(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
}
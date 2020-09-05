package com.e_comapp.android.exception

import android.text.TextUtils

/**
 *  Generalized Basic API Exception.  Subclasses will generally be used to specify exact exception
 */
open class ApiFailureException(message: String? = null, cause: Throwable? = null, code: Int?) :
    Exception(message) {
    private val _message: String? by lazy {
        if (!TextUtils.isEmpty(message)) {
            cause?.toString()
        } else {
            null
        }
    }

    private val _code: Int? by lazy {
        code
    }

    val code get() = _code

    override val message get() = _message ?: super.message

    init {
        if (cause != null) {
            super.initCause(cause)
        }
    }
}
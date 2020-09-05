package com.e_comapp.android.exception

import java.io.IOException

/**
 * Custom exception class which will let the user if no internet available
 * */
open class NoNetworkException : IOException() {

    override fun getLocalizedMessage(): String? = "No Internet Connection"
}
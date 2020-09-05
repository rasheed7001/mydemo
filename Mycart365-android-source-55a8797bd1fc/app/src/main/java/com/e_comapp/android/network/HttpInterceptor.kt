package com.e_comapp.android.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import com.e_comapp.android.exception.NoNetworkException
import com.e_comapp.android.preference.AppPreference
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.IOException

private const val AUTHORIZATION = "Authorization"

/**
 * Interceptor for network service which can set headers for requests
 * @param context is required to check the network state of the app
 * */
class HttpInterceptor(private val context: Context) :
    Interceptor, KoinComponent {

    private val preferenceManager: AppPreference by inject()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        if (!isConnected()) {
            throw NoNetworkException()
        }

        var request: Request = chain.request()

        val builder: Request.Builder = request.newBuilder()

        builder.header("Content-Type", "application/json")
        builder.header("type", "2")
        setAuthHeader(builder)

        request = builder.build()

        return chain.proceed(request)
    }

    private fun setAuthHeader(builder: Request.Builder) {
        val token = preferenceManager.getAppAccessToken()

        if (!TextUtils.isEmpty(token)) {
            builder.header(
                AUTHORIZATION, String.format(
                    "Bearer %s", token
                )
            )
        }
    }

    @Suppress("ComplexMethod", "ReturnCount", "DEPRECATION")
    private fun isConnected(): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {

                result = when (type) {
                    ConnectivityManager.TYPE_WIFI,
                    ConnectivityManager.TYPE_MOBILE,
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }

        return result
    }
}
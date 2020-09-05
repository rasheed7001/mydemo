package com.e_comapp.android.seller.api

import android.content.Context
import android.util.Log
import com.e_comapp.android.preference.AppPreference
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiClient(context: Context) {
    companion object {
        private var retrofit: Retrofit? = null
        private var appPreference: AppPreference? = null
        fun getApiClient(): ApiInterface {
            val defaultHttpClient = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            if (appPreference?.getAppAccessToken() == "") {
                                return chain.proceed(chain.request())
                            }
                            val authorisedRequest = chain.request().newBuilder().addHeader("Accept", "application/json").addHeader("Authorization", appPreference?.getAppAccessToken()
                                    ?: "")
                            Log.e("retrofit", "Authorization header is added to the url.... " + appPreference?.getAppAccessToken())
                            return chain.proceed(authorisedRequest.build())
                        }
                    })
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(URLs.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .client(defaultHttpClient)
                        .build()
            }
            return retrofit!!.create(ApiInterface::class.java)
        }
    }

    init {
        appPreference = AppPreference(context)
    }
}
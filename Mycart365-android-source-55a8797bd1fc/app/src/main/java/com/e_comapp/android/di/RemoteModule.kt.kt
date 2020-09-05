package com.e_comapp.android.di

import android.content.Context
import com.e_comapp.android.BuildConfig
import com.e_comapp.android.network.HttpInterceptor
import com.e_comapp.android.seller.api.ApiInterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "BASE_URL"
const val SOCKET_URL = "SOCKET_URL"
private const val TIME_OUT = "TIME_OUT"

/**
 * Remote Web Service data source
 */
val remoteDataSourceModule = module {

    factory {
        createOkHttpClient(get(), BuildConfig.TIME_OUT)
    }

    factory {
        createWebService<ApiInterface>(get(), BuildConfig.BASE_URL)
    }
}

/**
 * This method used to create okHttp client
 * */
fun createOkHttpClient(context: Context, timeOut: Long): OkHttpClient {

    val clientBuilder =
        OkHttpClient.Builder()
            .connectTimeout(timeOut, TimeUnit.SECONDS)
            .readTimeout(timeOut, TimeUnit.SECONDS)
            .writeTimeout(timeOut, TimeUnit.SECONDS)

    clientBuilder.addInterceptor(HttpInterceptor(context))

    if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(loggingInterceptor)
    }

    return clientBuilder.build()
}

/**
 * Method used to create Retrofit instance
 * @param [okHttpClient] used to bind with retrofit
 * */
inline fun <reified T> createWebService(okHttpClient: OkHttpClient, serverUrl: String): T {
    val retrofit =
        Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()

    return retrofit.create(T::class.java)
}
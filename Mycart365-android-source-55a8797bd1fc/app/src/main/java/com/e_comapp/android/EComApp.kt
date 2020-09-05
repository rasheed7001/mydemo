package com.e_comapp.android

import android.app.Application
import com.e_comapp.android.di.constantModules
import com.e_comapp.android.di.repoModule
import com.e_comapp.android.di.useCaseModule
import com.e_comapp.android.di.viewModelModules
import com.e_comapp.android.preference.AppPreference
import com.e_comapp.android.seller.api.ApiClient
import com.e_comapp.android.seller.api.ApiInterface
import com.facebook.drawee.backends.pipeline.Fresco
import net.one97.paytm.nativesdk.PaytmSDK
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level

class EComApp : Application() {
    var appPreference: AppPreference? = null
        private set
    private var appClient: ApiClient? = null

    //    private DatabaseHelper databaseHelper;
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        PaytmSDK.init(this)

        stopKoin()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@EComApp)
            modules(listOf(
                    viewModelModules,
                    constantModules, useCaseModule, repoModule
            ))
        }

        appPreference = AppPreference(this)
        appPreference!!.setAppBaseUrl("http://allupdates360.com/ecom/api/v1/")
        appClient = ApiClient(this)
        mApp = this
    }

    //    public DatabaseHelper getDatabaseHelper(){
    val retrofitInterface: ApiInterface
        get() = ApiClient.getApiClient()

    //        return databaseHelper;
    //    }
    companion object {
        private var mApp: EComApp? = null
        val app: EComApp?
            get() {
                if (mApp == null) {
                    mApp = EComApp()
                    mApp!!.onCreate()
                }
                return mApp
            }
    }
}
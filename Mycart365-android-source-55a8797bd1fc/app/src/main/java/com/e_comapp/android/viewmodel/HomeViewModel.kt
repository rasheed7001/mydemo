package com.e_comapp.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.e_comapp.android.preference.AppPreference
import com.e_comapp.android.seller.models.AppDefaultsParser
import com.e_comapp.android.viewmodel.base.CoroutinesViewModel
import com.google.gson.Gson

class HomeViewModel(private val appPreference: AppPreference) : CoroutinesViewModel() {

    private val _appDefault = MutableLiveData<AppDefaultsParser>()
    val appDefault: LiveData<AppDefaultsParser> = _appDefault

    init {
        _appDefault.value = Gson().fromJson(appPreference.getAppDefaults(), AppDefaultsParser::class.java)
    }

}
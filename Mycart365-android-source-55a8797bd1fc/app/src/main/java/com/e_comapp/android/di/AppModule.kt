package com.e_comapp.android.di

import com.e_comapp.android.preference.AppPreference
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * ScopeID for the current logged in scope
 */
const val MY_CART_SCOPE_ID = "MY_CART_SCOPE_ID"

/**
 * Qualifier for the logged in scope
 */
val MY_CART_SCOPE_QUALIFIER = named(MY_CART_SCOPE_ID)

val constantModules = module {
    single { AppPreference(androidContext()) }
}
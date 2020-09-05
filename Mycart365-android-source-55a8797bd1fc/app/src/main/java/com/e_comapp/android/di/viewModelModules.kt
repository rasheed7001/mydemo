package com.e_comapp.android.di

import com.e_comapp.android.viewmodel.HomeViewModel
import com.e_comapp.android.viewmodel.ProductListViewModel
import com.e_comapp.android.viewmodel.SellerListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {

    scope(MY_CART_SCOPE_QUALIFIER) {

        viewModel { HomeViewModel(get()) }

        viewModel { SellerListViewModel() }

        viewModel { ProductListViewModel() }
    }
}

val useCaseModule = module {

    /*scope(RIGHT_NOW_SCOPE_QUALIFIER) {
        scoped {
            CreateReviewUseCase(get())
        }
    }*/
}

val repoModule = module {

/*    scope(RIGHT_NOW_SCOPE_QUALIFIER) {
        scoped<AuthorizationRepo> {
            AuthorizationRepoImpl(get(), get())
        }
    }*/
}

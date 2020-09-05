package com.e_comapp.android.viewmodel.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.KoinComponent
import kotlin.coroutines.CoroutineContext

/**
 * ViewModel with coroutine scope that can be used by other ViewModels that use coroutines
 */
abstract class CoroutinesViewModel(parentJob: Job? = null) : ViewModel(), KoinComponent {

    protected val viewModelScope: CoroutineScope = ViewModelCoroutineScope(parentJob)

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }
}

private class ViewModelCoroutineScope(parentJob: Job?) : CoroutineScope {

    private val job = SupervisorJob(parentJob)
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
}

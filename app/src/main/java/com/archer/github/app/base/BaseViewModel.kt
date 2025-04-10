package com.archer.github.app.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.archer.github.app.logic.model.ErrorType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    protected val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    protected val _errorState: MutableSharedFlow<ErrorType> = MutableSharedFlow()
    val errorState = _errorState

    protected fun launchWithErrorHandler(
        onError: (ErrorType) -> Unit = { handleApiError(it) },
        block: suspend () -> Unit
    ) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            onError(ErrorType.NETWORK_EXCEPTION)
        }

        viewModelScope.launch(exceptionHandler) {
            block()
        }
    }

    protected open fun handleApiError(error: ErrorType) {
        viewModelScope.launch {
            _errorState.emit(error)
        }
    }
}

package com.development.hiltpractices

import androidx.lifecycle.viewModelScope
import com.development.hiltpractices.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainActivityViewModel : BaseViewModel() {

    private val _isLoadSuccess = MutableStateFlow(false)
    val isLoadSuccess = _isLoadSuccess

    fun loadData() {
        viewModelScope.launch {
//            delay(3000)
            _isLoadSuccess.emit(true)
        }
    }
}
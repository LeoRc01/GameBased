package com.cip.cipstudio.model.data

import androidx.lifecycle.MutableLiveData

class Loading {
    val isPageLoading : MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>(true)
    }
}
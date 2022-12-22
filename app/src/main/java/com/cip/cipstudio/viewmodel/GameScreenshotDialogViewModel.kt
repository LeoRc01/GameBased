package com.cip.cipstudio.viewmodel

import android.content.res.Configuration
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameScreenshotDialogViewModel : ViewModel() {
    val orientationLiveData : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(Configuration.ORIENTATION_PORTRAIT)
    }
}

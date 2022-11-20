package com.cip.cipstudio.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cip.cipstudio.databinding.FragmentEmailChangeBinding

class ChangeEmailViewModel(changeEmailBinding: FragmentEmailChangeBinding) {
    private val TAG = "ChangeEmailViewModel"

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun changeEmail(onSuccess: () -> Unit,
                    onFailure: () -> Unit = {}) {
        Log.e(TAG, "DA IMPLEMENTARE")
    }

}
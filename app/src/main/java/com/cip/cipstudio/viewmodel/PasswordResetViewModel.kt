package com.cip.cipstudio.viewmodel

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.utils.AuthErrorEnum

class PasswordResetViewModel(context: Context) : ViewModel() {

    var email: MutableLiveData<String> = MutableLiveData()

    fun resetPassword(onSuccess: () -> Unit,
                      onFailure: (AuthErrorEnum) -> Unit = {}) {

        val email = this.email.value.toString().trim()

        if (!isValidEmail(email)) {
            onFailure(AuthErrorEnum.EMAIL_NOT_VALID)
            return
        }

        //TODO: reset password
        onSuccess.invoke()
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
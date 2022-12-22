package com.cip.cipstudio.viewmodel

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.model.User.email
import com.cip.cipstudio.utils.AuthErrorEnum
import com.google.firebase.auth.FirebaseAuth

class PasswordResetViewModel() : ViewModel() {

    var email: MutableLiveData<String> = MutableLiveData()

    fun resetPassword(onSuccess: () -> Unit,
                      onFailure: (AuthErrorEnum) -> Unit = {}) {

        val email = this.email.value.toString().trim()

        if (!isValidEmail(email)) {
            onFailure(AuthErrorEnum.EMAIL_NOT_VALID)
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener {
            onSuccess.invoke()
        }.addOnFailureListener {
            onFailure(AuthErrorEnum.EMAIL_NOT_FOUND)
        }

    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
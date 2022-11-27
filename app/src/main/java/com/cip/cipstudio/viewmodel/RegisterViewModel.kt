package com.cip.cipstudio.viewmodel

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.utils.Validator.Companion.isValidEmail
import com.cip.cipstudio.utils.Validator.Companion.isValidPassword
import com.cip.cipstudio.utils.Validator.Companion.isValidUsername
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterViewModel() : ViewModel() {

    private val TAG = "RegisterViewModel"

    var email: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    var confirmPassword: MutableLiveData<String> = MutableLiveData()
    var username: MutableLiveData<String> = MutableLiveData()

    fun register(context: Context,
                onSuccess: () -> Unit,
                 onFailure: (AuthErrorEnum) -> Unit = {}) {

        val email = this.email.value.toString().trim()
        val password = this.password.value.toString()
        val confirmPassword = this.confirmPassword.value.toString()
        val username = this.username.value.toString()

        Log.e(TAG, "email: $email")
        Log.e(TAG, "password: $password")
        Log.e(TAG, "confirmPassword: $confirmPassword")
        Log.e(TAG, "username: $username")

        if (isValidUsername(username) != null) {
            onFailure(isValidUsername(username)!!)
            return
        }

        if (!isValidEmail(email)) {
            onFailure(AuthErrorEnum.EMAIL_NOT_VALID)
            return
        }

        if (isValidPassword(password) != null) {
            onFailure(isValidPassword(password)!!)
            return
        }

        if (password != confirmPassword) {
            onFailure(AuthErrorEnum.PASSWORDS_NOT_MATCH)
            return
        }

        LoadingSpinner.showLoadingDialog(context)

        User.register(email, password, username)
            .addOnSuccessListener {
                LoadingSpinner.dismiss()
                onSuccess.invoke()
            }
            .addOnFailureListener {
                LoadingSpinner.dismiss()
                when (it) {
                    is FirebaseAuthUserCollisionException -> {
                        onFailure(AuthErrorEnum.EMAIL_ALREADY_IN_USE)
                    }
                    else -> {
                        onFailure(AuthErrorEnum.UNKNOWN_ERROR)
                    }
                }
            }
    }
}

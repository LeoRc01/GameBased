package com.cip.cipstudio.viewmodel

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.R
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterViewModel() : ViewModel() {

    private val TAG = "RegisterViewModel"

    var email: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    var confirmPassword: MutableLiveData<String> = MutableLiveData()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun register(context: Context,
                onSuccess: () -> Unit,
                 onFailure: (AuthErrorEnum) -> Unit = {}) {

        val email = this.email.value.toString().trim()
        val password = this.password.value.toString()
        val confirmPassword = this.confirmPassword.value.toString()

        Log.e(TAG, "email: $email")
        Log.e(TAG, "password: $password")
        Log.e(TAG, "confirmPassword: $confirmPassword")

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

        auth.createUserWithEmailAndPassword(email, password)
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

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }



    private fun isValidPassword(password: String): AuthErrorEnum? {
        when {
            password.length < 8 -> return AuthErrorEnum.PASSWORD_TOO_SHORT
            password.length > 20 -> return AuthErrorEnum.PASSWORD_TOO_LONG
            !password.matches(Regex(".*\\d.*")) -> return AuthErrorEnum.PASSWORD_NO_DIGIT
            !password.matches(Regex(".*[a-z].*")) -> return AuthErrorEnum.PASSWORD_NO_LOWERCASE
            !password.matches(Regex(".*[A-Z].*")) -> return AuthErrorEnum.PASSWORD_NO_UPPERCASE
            !password.matches(Regex(".*[!@#\$%^&*()_+].*")) -> return AuthErrorEnum.PASSWORD_NO_SPECIAL_CHARACTER
            else -> return null
        }
    }
}

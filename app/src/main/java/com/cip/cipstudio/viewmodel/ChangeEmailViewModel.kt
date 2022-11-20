package com.cip.cipstudio.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import com.cip.cipstudio.databinding.FragmentEmailChangeBinding
import com.cip.cipstudio.utils.AuthErrorEnum
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class ChangeEmailViewModel(changeEmailBinding: FragmentEmailChangeBinding) {
    private val TAG = "ChangeEmailViewModel"

    var email : MutableLiveData<String> = MutableLiveData()
    var password : MutableLiveData<String> = MutableLiveData()
    private val user = FirebaseAuth.getInstance().currentUser

    fun changeEmail(onSuccess: () -> Unit,
                    onFailure: (AuthErrorEnum) -> Unit = {}) {

        val newEmail = this.email.value.toString().trim()
        val password = this.password.value.toString()
        Log.e(TAG, "newEmail: $newEmail")
        Log.e(TAG, "password: $password")


        if(!isValidEmail(newEmail)) {
            onFailure(AuthErrorEnum.EMAIL_NOT_VALID)
            return
        }

        val credential = EmailAuthProvider.getCredential(user?.email.toString(), password)

        user?.reauthenticate(credential)?.addOnCompleteListener { Log.d(TAG, "User re-authenticated.") }

        user?.updateEmail(newEmail)?.addOnSuccessListener {
                Log.d(TAG, "User email address updated.")
                onSuccess.invoke()
        }?.addOnFailureListener {
                when(it){
                    is FirebaseAuthUserCollisionException -> {
                        onFailure(AuthErrorEnum.EMAIL_ALREADY_IN_USE)
                    }
                    is FirebaseAuthEmailException -> {
                        onFailure(AuthErrorEnum.EMAIL_NOT_VALID)
                    }
                    is FirebaseAuthRecentLoginRequiredException -> {
                        onFailure(AuthErrorEnum.RECENT_LOGIN_REQUIRED)
                    }
                    else -> {
                        onFailure(AuthErrorEnum.UNKNOWN_ERROR)
                    }
                }
        }

    }

    private fun isValidEmail(email: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
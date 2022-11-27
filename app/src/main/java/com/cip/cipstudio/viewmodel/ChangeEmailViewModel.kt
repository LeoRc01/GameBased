package com.cip.cipstudio.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import com.cip.cipstudio.databinding.FragmentEmailChangeBinding
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.utils.Validator.Companion.isValidEmail
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class ChangeEmailViewModel(changeEmailBinding: FragmentEmailChangeBinding) {
    private val TAG = "ChangeEmailViewModel"

    var email : MutableLiveData<String> = MutableLiveData()
    var password : MutableLiveData<String> = MutableLiveData()
    private val user = User

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


        if(newEmail == user.email) {
            onFailure(AuthErrorEnum.EMAIL_ALREADY_YOURS)
            return
        }


        user.reauthenticate(user.email!!,password).addOnSuccessListener {
            user.updateEmail(newEmail).addOnSuccessListener {
                Log.d(TAG, "User email address updated.")
                onSuccess.invoke()
            }.addOnFailureListener {
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
        }.addOnFailureListener{
            when(it){
                is FirebaseAuthRecentLoginRequiredException -> {
                    onFailure(AuthErrorEnum.RECENT_LOGIN_REQUIRED)
                }
                else -> {
                    onFailure(AuthErrorEnum.WRONG_PASSWORD)
                }
            }
        }



    }

}
package com.cip.cipstudio.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.exception.NotLoggedException
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.utils.Validator.Companion.isValidPassword
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException

class ChangePasswordViewModel() : ViewModel(){
    private val TAG = "ChangePasswordViewModel"

    var oldPassword : MutableLiveData<String> = MutableLiveData()
    var newPassword : MutableLiveData<String> = MutableLiveData()
    var newPasswordConfirm : MutableLiveData<String> = MutableLiveData()
    private val user = User

    fun changePassword(onSuccess: () -> Unit,
                       onFailure: (AuthErrorEnum) -> Unit = {}) {

        val oldPassword = this.oldPassword.value.toString()
        val newPassword = this.newPassword.value.toString()
        val newPasswordConfirm = this.newPasswordConfirm.value.toString()


        if (isValidPassword(newPassword) != null) {
            onFailure(isValidPassword(newPassword)!!)
            return
        }


        if(newPassword != newPasswordConfirm) {
            onFailure(AuthErrorEnum.PASSWORDS_NOT_MATCH)
            return
        }



        if (oldPassword == newPassword) {
            onFailure(AuthErrorEnum.SAME_PASSWORD)
            return
        }

        user.reauthenticate(user.email!!,oldPassword).addOnSuccessListener {
            user.updatePassword(newPassword).addOnSuccessListener {
                Log.d(TAG, "User password updated.")
                onSuccess.invoke()
            }.addOnFailureListener {
                when(it){
                    is FirebaseAuthRecentLoginRequiredException -> {
                        onFailure(AuthErrorEnum.RECENT_LOGIN_REQUIRED)
                    }
                    is NotLoggedException -> {
                        onFailure(AuthErrorEnum.NOT_LOGGED)
                    }
                    else -> {
                        onFailure(AuthErrorEnum.UNKNOWN_ERROR)
                    }
                }
            }
        }.addOnFailureListener {
            val exception = if (it is NotLoggedException) {
                AuthErrorEnum.NOT_LOGGED
            } else {
                AuthErrorEnum.WRONG_PASSWORD
            }
            onFailure(exception)
        }


    }

}

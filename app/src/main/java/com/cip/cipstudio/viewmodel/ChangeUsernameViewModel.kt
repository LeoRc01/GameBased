package com.cip.cipstudio.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.exception.NotLoggedException
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.utils.Validator.Companion.isValidUsername
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.UserProfileChangeRequest

class ChangeUsernameViewModel : ViewModel() {
    private val TAG = "ChangeUsernameViewModel"
    private val user = User
    var newUsername: MutableLiveData<String> = MutableLiveData()

    fun changeUsername( onSuccess: () -> Unit,
                        onFailure: (AuthErrorEnum) -> Unit = {}) {

        val newUsername = this.newUsername.value.toString()


        if (isValidUsername(newUsername) != null) {
            onFailure(isValidUsername(newUsername)!!)
            return
        }

       UserProfileChangeRequest.Builder()
            .setDisplayName(newUsername)
            .build()
            .let { user.updateUsername(it) }
            ?.addOnSuccessListener {
                Log.d(TAG, "User profile updated.")
                onSuccess.invoke()
            }
            ?.addOnFailureListener {
                when (it) {
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
    }


}
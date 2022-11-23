package com.cip.cipstudio.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cip.cipstudio.databinding.FragmentPasswordChangeBinding
import com.cip.cipstudio.model.User.email
import com.cip.cipstudio.utils.AuthErrorEnum
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException

class ChangePasswordViewModel(changePasswordBinding: FragmentPasswordChangeBinding) {
    private val TAG = "ChangePasswordViewModel"

    var oldPassword : MutableLiveData<String> = MutableLiveData()
    var newPassword : MutableLiveData<String> = MutableLiveData()
    var newPasswordConfirm : MutableLiveData<String> = MutableLiveData()
    private val user = FirebaseAuth.getInstance().currentUser

    fun changePassword(onSuccess: () -> Unit,
                       onFailure: (AuthErrorEnum) -> Unit = {}) {

        val oldPassword = this.oldPassword.value.toString()
        val newPassword = this.newPassword.value.toString()
        val newPasswordConfirm = this.newPasswordConfirm.value.toString()

        Log.e(TAG, "oldPassword: $oldPassword")
        Log.e(TAG, "newPassword: $newPassword")
        Log.e(TAG, "newPasswordConfirm: $newPasswordConfirm")

        if (isValidPassword(newPassword) != null) {
            onFailure(isValidPassword(newPassword)!!)
            return
        }


        if(newPassword != newPasswordConfirm) {
            onFailure(AuthErrorEnum.PASSWORDS_NOT_MATCH)
            return
        }


        val credential = EmailAuthProvider.getCredential(user?.email.toString(), oldPassword)


        if (oldPassword == newPassword) {
            onFailure(AuthErrorEnum.SAME_PASSWORD)
            return
        }

        user?.reauthenticate(credential)?.addOnSuccessListener {
            user.updatePassword(newPassword).addOnSuccessListener {
                Log.d(TAG, "User password updated.")
                onSuccess.invoke()
            }.addOnFailureListener {
                when(it){
                    is FirebaseAuthRecentLoginRequiredException -> {
                        onFailure(AuthErrorEnum.RECENT_LOGIN_REQUIRED)
                    }
                    else -> {
                        onFailure(AuthErrorEnum.UNKNOWN_ERROR)
                    }
                }
            }
        }?.addOnFailureListener {
            onFailure(AuthErrorEnum.WRONG_PASSWORD)
        }


    }

    private fun isValidPassword(password: String): AuthErrorEnum? {
        return when {
            password.length < 8 -> AuthErrorEnum.PASSWORD_TOO_SHORT
            password.length > 20 -> AuthErrorEnum.PASSWORD_TOO_LONG
            !password.matches(Regex(".*\\d.*")) -> AuthErrorEnum.PASSWORD_NO_DIGIT
            !password.matches(Regex(".*[a-z].*")) -> AuthErrorEnum.PASSWORD_NO_LOWERCASE
            !password.matches(Regex(".*[A-Z].*")) -> AuthErrorEnum.PASSWORD_NO_UPPERCASE
            !password.matches(Regex(".*[!@#\$%^&*()_+].*")) -> AuthErrorEnum.PASSWORD_NO_SPECIAL_CHARACTER
            else -> null
        }
    }
}

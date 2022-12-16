package com.cip.cipstudio.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.dataSource.repository.historyRepository.HistoryRepositoryLocal
import com.cip.cipstudio.databinding.FragmentLoginBinding
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.utils.Validator.Companion.isValidEmail
import com.cip.cipstudio.utils.Validator.Companion.isValidLoginPassword
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginViewModel(val binding: FragmentLoginBinding) : ViewModel(){

    private val TAG = "LoginViewModel"

    var email : MutableLiveData<String> = MutableLiveData()
    var password : MutableLiveData<String> = MutableLiveData()



    fun login(onSuccess: () -> Unit,
              onFailure: (AuthErrorEnum) -> Unit = {}) {

        val email = this.email.value.toString().trim()
        val password = this.password.value.toString()

        Log.e(TAG,"email: $email")
        Log.e(TAG,"password: $password")

        if(!isValidEmail(email)) {
            onFailure(AuthErrorEnum.EMAIL_NOT_VALID)
            return
        }

        if(!isValidLoginPassword(password)){
            onFailure(AuthErrorEnum.PASSWORD_NOT_CORRECT)
            return
        }

        User.login(email, password)
            .addOnSuccessListener {
                onSuccess.invoke()
            }
            .addOnSuccessListener {
                User.syncRecentlyViewedGames(HistoryRepositoryLocal(binding.root.context))
            }
            .addOnFailureListener {
                LoadingSpinner.dismiss()
                when(it){
                    is FirebaseAuthInvalidUserException -> {
                        onFailure(AuthErrorEnum.EMAIL_NOT_REGISTERED)
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        onFailure(AuthErrorEnum.PASSWORD_NOT_CORRECT)
                    }
                    else -> {
                        onFailure(AuthErrorEnum.UNKNOWN_ERROR)
                    }
                }
            }
    }
}
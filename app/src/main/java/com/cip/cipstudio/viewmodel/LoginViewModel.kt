package com.cip.cipstudio.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginViewModel() : ViewModel(){

    private val TAG = "LoginViewModel"

    var email : MutableLiveData<String> = MutableLiveData()
    var password : MutableLiveData<String> = MutableLiveData()

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()


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

        if(!isValidPassword(password)){
            onFailure(AuthErrorEnum.PASSWORD_NOT_CORRECT)
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess.invoke()
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

    private fun isValidEmail(email: String) : Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String) : Boolean {
        val PASSWORD_REGEX = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#${'$'}%!\-_?&])(?=\S+${'$'}).{8,20}${'$'}""".toRegex()
        return PASSWORD_REGEX.matches(password)
    }

}
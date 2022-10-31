package com.cip.cipstudio.viewmodel

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.R
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class AuthViewModel(val context : Context) : ViewModel(){

    private val TAG = "AuthViewModel"

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

        LoadingSpinner.showLoadingDialog(context)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                LoadingSpinner.dismiss()
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

    fun register(email : String,
                 password : String,
                 passwordConfirm: String,
                 emailLayout : TextInputLayout,
                 pwdLayout: TextInputLayout,
                 pwdConfirmLayout: TextInputLayout,
                 onSuccess: () -> Unit) {

        var canLogin = true

        if(!isValidEmail(email)){
            emailLayout.error = context.getString(R.string.email_not_valid)
            canLogin = false
        }

        if(!isValidPassword(password)){
            when {
                password.length < 8 -> pwdLayout.error = context.getString(R.string.password_too_short)
                password.length > 20 -> pwdLayout.error = context.getString(R.string.password_too_long)
                !password.matches(Regex(".*\\d.*")) -> pwdLayout.error = context.getString(R.string.password_no_digit)
                !password.matches(Regex(".*[a-z].*")) -> pwdLayout.error = context.getString(R.string.password_no_lowercase)
                !password.matches(Regex(".*[A-Z].*")) -> pwdLayout.error = context.getString(R.string.password_no_uppercase)
                !password.matches(Regex(".*[!@#\$%^&*()_+].*")) -> pwdLayout.error = context.getString(R.string.password_no_special_character)
            }
            canLogin = false
        }

        if(passwordConfirm != password){
            pwdConfirmLayout.error = context.getString(R.string.passwords_not_match)
            canLogin = false
        }

        if(!canLogin)
            return

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener{
                onSuccess.invoke()
                Toast.makeText(context, context.getString(R.string.registered), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                LoadingSpinner.dismiss()
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
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
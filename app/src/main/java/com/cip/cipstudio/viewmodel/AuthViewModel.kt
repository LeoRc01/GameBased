package com.cip.cipstudio.viewmodel

import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.R
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlin.text.Typography.registered

class AuthViewModel(val context : Context) : ViewModel(){

    var email : MutableLiveData<String> = MutableLiveData()
    var password : MutableLiveData<String> = MutableLiveData()

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()


    val isLoginModeLiveData : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun login(emailLayout : TextInputLayout,
              pwdLayout: TextInputLayout,
              onSuccess: () -> Unit) {
        val email = this.email.value.toString().trim()
        val password = this.password.value.toString()

        Log.e("AUTHVIEWMODEL","email: $email")
        Log.e("AUTHVIEWMODEL","password: $password")

        var canLogin = true

        if(!isValidEmail(email)){
            emailLayout.error = context.getString(R.string.invalid_email)
            canLogin = false
        }

        if(!isValidPassword(password)){
            pwdLayout.error = context.getString(R.string.invalid_password)
            canLogin = false
        }

        if(!canLogin)
            return

        LoadingSpinner.showLoadingDialog(context)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                LoadingSpinner.dismiss()
                onSuccess.invoke()
            }
            .addOnFailureListener {
                LoadingSpinner.dismiss()
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
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
            emailLayout.error = context.getString(R.string.invalid_email)
            canLogin = false
        }

        if(!isValidPassword(password)){
            when {
                password.length < 8 -> pwdLayout.error = context.getString(R.string.short_password)
                password.length > 20 -> pwdLayout.error = context.getString(R.string.long_password)
                !password.matches(Regex(".*\\d.*")) -> pwdLayout.error = context.getString(R.string.no_number)
                !password.matches(Regex(".*[a-z].*")) -> pwdLayout.error = context.getString(R.string.no_lowercase)
                !password.matches(Regex(".*[A-Z].*")) -> pwdLayout.error = context.getString(R.string.no_uppercase)
                !password.matches(Regex(".*[!@#\$%^&*()_+].*")) -> pwdLayout.error = context.getString(R.string.no_special_character)
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
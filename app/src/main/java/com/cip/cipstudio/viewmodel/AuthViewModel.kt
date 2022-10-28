package com.cip.cipstudio.viewmodel

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel(val context : Context) : ViewModel(){

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()


    val isLoginModeLiveData : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun login(email : String,
              password : String,
              emailLayout : TextInputLayout,
              pwdLayout: TextInputLayout,
              onSuccess: () -> Unit) {
        var canLogin = true

        if(!isValidEmail(email)){
            emailLayout.error = "@string/invalid_email"
            canLogin = false
        }

        if(!isValidPassword(password)){
            pwdLayout.error = "Password cannot be empty."
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
            emailLayout.error = "Not a valid email."
            canLogin = false
        }

        if(!isValidPassword(password)){
            pwdLayout.error = "Password not valid."
            canLogin = false
        }

        if(passwordConfirm != password){
            pwdConfirmLayout.error = "Passwords do not match."
            canLogin = false
        }

        if(!canLogin)
            return

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener{
                onSuccess.invoke()
                Toast.makeText(context, "Registered", Toast.LENGTH_SHORT).show()
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
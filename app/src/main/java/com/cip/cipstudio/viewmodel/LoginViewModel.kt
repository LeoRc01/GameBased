package com.cip.cipstudio.viewmodel

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.R
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception


class LoginViewModel : ViewModel() {

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    /**
     *
     * context : contesto dell'activity
     *
     * pwdEt / emailEt: EditText contenente il testo
     *
     * emailLayout / pwdLayout: il Layout per mostrare poi l'errore (se necessario)
     *
     */

    fun login(context : Context,
              emailEt: TextInputEditText,
              pwdEt: TextInputEditText,
              emailLayout : TextInputLayout,
              pwdLayout: TextInputLayout){

        var canLogin = true

        if(!isValidEmail(emailEt.text!!.trim().toString()) || emailEt.text!!.isEmpty()){

            emailLayout.error = "Not a valid email."
            canLogin = false
        }

        if(pwdEt.text!!.isEmpty()){

            pwdLayout.error = "Password is empty."
            canLogin = false
        }
        if(!canLogin)
            return

        val email : String = emailEt.text!!.trim().toString()
        val pwd : String = pwdEt.text.toString()

        auth.signInWithEmailAndPassword(email, pwd).addOnSuccessListener {
            Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT).show()
        }
    }

    /**
    *
    * Controlla che la mail sia valida
    *
    * */

    fun isValidEmail(email: String): Boolean {
        return if (TextUtils.isEmpty(email)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }

}
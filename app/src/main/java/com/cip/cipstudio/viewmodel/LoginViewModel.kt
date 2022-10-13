package com.cip.cipstudio.viewmodel

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.R
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth


class LoginViewModel(val context : Context) : ViewModel(){

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private var isLoginMode : Boolean = true

    /**
     *
     * isLoginModeLiveData contiene il dato osservabile dall'activity
     * per controllare se l'utente vuole registrarsi o fare il login
     *
    * */

    val isLoginModeLiveData : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun switchLoginMode(){
        isLoginMode = !isLoginMode
        isLoginModeLiveData.value = isLoginMode
    }

    /**
     *
     * execute esegue il Login in caso siamo in LoginMode,
     * mentre esegue la registrazione se siamo in RegisterMode
     *
     * context : contesto dell'activity
     *
     * pwdEt / emailEt: EditText contenente il testo
     *
     * emailLayout / pwdLayout: il Layout per mostrare poi l'errore (se necessario)
     *
     * onSuccess è la funzione callback che viene eseguita quando è
     * è stato eseguito login/register
     */

    fun execute(emailEt: TextInputEditText,
              pwdEt: TextInputEditText,
              emailLayout : TextInputLayout,
              pwdLayout: TextInputLayout,
              onSuccess: () -> Unit
              ){

        var canLogin = true

        if(!isValidEmail(emailEt.text!!.trim().toString()) || emailEt.text!!.isEmpty()){
            emailLayout.error = "Not a valid email."
            canLogin = false
        }

        if(pwdEt.text!!.isEmpty()){

            pwdLayout.error = "Password is empty."
            canLogin = false
        }

        /** Se ci sono degli errori esco dalla funzione */
        if(!canLogin)
            return

        LoadingSpinner.showLoadingDialog(context)

        val email : String = emailEt.text!!.trim().toString()
        val pwd : String = pwdEt.text.toString()

        if(isLoginMode){
            auth.signInWithEmailAndPassword(email, pwd).addOnSuccessListener {
                LoadingSpinner.dismiss()
                onSuccess.invoke()
            }.addOnFailureListener{
                LoadingSpinner.dismiss()
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }else{
            auth.createUserWithEmailAndPassword(email, pwd)
                .addOnSuccessListener {
                    onSuccess.invoke()
                    Toast.makeText(context, "Registered", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    LoadingSpinner.dismiss()
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
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
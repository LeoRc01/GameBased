package com.cip.cipstudio.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cip.cipstudio.R
import com.cip.cipstudio.viewmodel.LoginViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initalizeLoginButton()

    }

    fun initalizeLoginButton(){
        val loginViewModel : LoginViewModel = LoginViewModel()
        val loginBtn = findViewById<Button>(R.id.btnLogin)

        val emailEt = findViewById<TextInputEditText>(R.id.emailEt)
        val pwdEt = findViewById<TextInputEditText>(R.id.pwdEt)

        val emailLayout = findViewById<TextInputLayout>(R.id.tfEmail)
        val pwdLayout = findViewById<TextInputLayout>(R.id.tfPassword)

        loginBtn.setOnClickListener {
            loginViewModel.login(this, emailEt, pwdEt, emailLayout, pwdLayout)
        }
    }
}
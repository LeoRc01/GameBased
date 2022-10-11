package com.cip.cipstudio.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cip.cipstudio.R
import com.cip.cipstudio.viewmodel.LoginViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    // ViewModel dell'Activity
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var switchModeButton: TextView
    private lateinit var loginBtn : Button
    private lateinit var emailEt : TextInputEditText
    private lateinit var pwdEt : TextInputEditText
    private lateinit var emailLayout : TextInputLayout
    private lateinit var pwdLayout : TextInputLayout
    private lateinit var tvMode : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn  = findViewById<Button>(R.id.btnLogin)
        emailEt = findViewById<TextInputEditText>(R.id.emailEt)
        pwdEt  = findViewById<TextInputEditText>(R.id.pwdEt)
        emailLayout = findViewById<TextInputLayout>(R.id.emailLayout)
        pwdLayout  = findViewById<TextInputLayout>(R.id.pwdLayout)
        tvMode = findViewById<TextView>(R.id.tvMode)

        // Nascondo l'actionBar
        supportActionBar!!.hide()

        // Inizializzo il ViewModel
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        loginViewModel.isLoginModeLiveData.observe(this, Observer{
            if (it)
                setUIToLogin()
            else
                setUIToRegister()
        })

        // Inizializzo il bottone login
        initalizeLoginButton()

        // Inizializzo il bottone per il cambio modalità
        initSwitchModeButton()

    }

    /**
     *
     * Imposta la UI per il Login dell'utente
     *
     * */

    private fun setUIToLogin(){
        switchModeButton.text = "Register"
        tvMode.text = "Login"
        findViewById<TextView>(R.id.tvForgotPwd).visibility = View.VISIBLE
        initalizeLoginButton()
    }

    /**
     *
     * Imposta la UI per la fase di registrazione dell'utente
     *
     * */

    private fun setUIToRegister(){
        switchModeButton.text = "Login"
        tvMode.text = "Register"
        findViewById<TextView>(R.id.tvForgotPwd).visibility = View.GONE
        initalizeRegisterButton()
    }

    /**
     *
     * Inizializza il bottone per switchare le modalità
     *
     * */

    private fun initSwitchModeButton(){
        switchModeButton = findViewById<TextView>(R.id.tvSwitchMode)
        switchModeButton.setOnClickListener {
            loginViewModel.switchLoginMode()
        }
    }

    /**
     *
     * Imposta il bottone su Login
     *
     * */

    private fun initalizeLoginButton(){

        loginBtn.text = "Login"
        loginBtn.setOnClickListener {
            loginViewModel.login(this, emailEt, pwdEt, emailLayout, pwdLayout)
        }
    }

    /**
     *
     * Imposta il bottone su Register
     *
     * */

    private fun initalizeRegisterButton(){
        val loginBtn : Button = findViewById<Button>(R.id.btnLogin)
        val emailEt : TextInputEditText= findViewById<TextInputEditText>(R.id.emailEt)
        val pwdEt : TextInputEditText = findViewById<TextInputEditText>(R.id.pwdEt)
        val emailLayout : TextInputLayout= findViewById<TextInputLayout>(R.id.emailLayout)
        val pwdLayout : TextInputLayout = findViewById<TextInputLayout>(R.id.pwdLayout)
        loginBtn.text = "Register"
        loginBtn.setOnClickListener {
            loginViewModel.register(this, emailEt, pwdEt, emailLayout, pwdLayout)
        }
    }
}
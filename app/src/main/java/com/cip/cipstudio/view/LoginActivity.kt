package com.cip.cipstudio.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import okhttp3.RequestBody.Companion.toRequestBody
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.api.igdb.apicalypse.APICalypse
import com.api.igdb.request.IGDBWrapper
import com.api.igdb.request.IGDBWrapper.apiProtoRequest
import com.api.igdb.request.TwitchAuthenticator
import com.api.igdb.request.games
import com.api.igdb.utils.Endpoints
import com.api.igdb.utils.TwitchToken
import com.cip.cipstudio.R
import com.cip.cipstudio.repository.IGDBRepository
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.cip.cipstudio.viewmodel.LoginViewModel
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Reserved
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import org.json.JSONObject
import proto.Game
import proto.GameResult
import java.io.IOException

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

    private lateinit var navController: NavController

    // Kotlin example



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser!=null){
            startMainActivity()
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        navController = navHostFragment.navController

        // Nascondo l'actionBar
        supportActionBar!!.hide()

        // Inizializzo il ViewModel
        // loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        loginViewModel = LoginViewModel(this)

        loginViewModel.isLoginModeLiveData.observe(this, Observer{
            //if (it)
                // setUIToLogin()
            //else
                // setUIToRegister()
        })

        // Inizializzo il bottone login
        //initalizeLoginButton()

        // Inizializzo il bottone per il cambio modalità
        //initSwitchModeButton()



    }

    /**
     *
     * Imposta la UI per il Login dell'utente
     *
     * */

    /*private fun setUIToLogin(){
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
            loginViewModel.execute( emailEt, pwdEt, emailLayout, pwdLayout) {
                startMainActivity()
            }
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
            loginViewModel.execute(emailEt, pwdEt, emailLayout, pwdLayout) {
                startMainActivity()
            }
        }
    }*/

    private fun startMainActivity(){
        val mainActivity: Intent = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
        finish()
    }
}

package com.cip.cipstudio.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.cip.cipstudio.R
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var preferences : android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences(getString(R.string.setting_preferences), MODE_PRIVATE)

        if (preferences.contains(getString(R.string.dark_mode_settings))) {
            preferences.edit().putBoolean(getString(R.string.dark_mode_settings), true).apply()
        }

        if (preferences.getBoolean(getString(R.string.dark_mode_settings), false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }



        setContentView(R.layout.activity_auth)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser!=null){
            startMainActivity()
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        navController = navHostFragment.navController

        // Nascondo l'actionBar
        supportActionBar!!.hide()
    }

    private fun startMainActivity(){
        val mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
        finish()
    }
}

package com.cip.cipstudio.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.cip.cipstudio.R
import com.cip.cipstudio.repository.IGDBWrappermio
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val mainActivity: Intent = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
        finish()
    }
}

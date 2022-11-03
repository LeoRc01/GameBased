package com.cip.cipstudio.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.cip.cipstudio.R
import com.cip.cipstudio.repository.IGDBRepository
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var gameRepo : IGDBRepository
    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gameRepo = IGDBRepository()

        supportActionBar!!.hide()


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fCVactivity_main) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)


        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }
}

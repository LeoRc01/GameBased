package com.cip.cipstudio.view

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.cip.cipstudio.R
import com.cip.cipstudio.view.fragment.FavouriteFragment
import com.cip.cipstudio.view.fragment.MainPageFragment
import com.cip.cipstudio.view.fragment.SearchFragment
import com.cip.cipstudio.view.fragment.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    lateinit var bottomNavigationView : BottomNavigationView
    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.a_main_cv_container) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.a_main_bnv_bottom_navigation)

        bottomNavigationView.setOnItemReselectedListener {
            val currentFragment = navHostFragment.childFragmentManager.fragments[0]
            when(it.itemId){
                R.id.menu_home->{
                    if(currentFragment !is MainPageFragment){
                        navController.navigate(R.id.action_global_homeScreen)
                        navController.clearBackStack("")
                    }else{
                        navController.navigate(R.id.action_homeScreen_self)
                        navController.clearBackStack("")
                    }
                }
                R.id.menu_favourite->{
                    if(currentFragment !is FavouriteFragment) {
                        navController.navigate(R.id.action_global_favouriteScreen)
                        navController.clearBackStack("")
                    }
                }
                R.id.menu_search->{
                    if(currentFragment !is SearchFragment) {
                        navController.navigate(R.id.action_global_searchScreen)
                        navController.clearBackStack("")
                    }
                }
                R.id.menu_profile->{
                    if(currentFragment !is UserFragment) {
                    }
                }
            }
        }

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    fun loadFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.a_main_cv_container, fragment, "")
            .addToBackStack(null)
            .commit();
    }
}

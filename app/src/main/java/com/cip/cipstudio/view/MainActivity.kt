package com.cip.cipstudio.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.cip.cipstudio.R
import com.cip.cipstudio.utils.ContextWrapper
import com.cip.cipstudio.view.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
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
                    when(currentFragment){
                        is GameListFragment -> {
                            navController.navigate(R.id.action_gameListFragment_to_homeScreen)
                            navController.clearBackStack("")
                        }
                        !is MainPageFragment -> {
                            navController.navigate(R.id.action_global_homeScreen)
                            navController.clearBackStack("")
                        }
                        else -> {
                            navController.clearBackStack("")
                        }
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
                    when(currentFragment){
                        is UserFragment -> {
                            navController.clearBackStack("")
                        }
                        is ChangeUsernameFragment -> {
                            navController.navigate(R.id.action_changeUsernameFragment_to_userFragment)
                            navController.clearBackStack("")
                        }
                        is ChangePasswordFragment -> {
                            navController.navigate(R.id.action_changePasswordFragment_to_userFragment)
                            navController.clearBackStack("")
                        }
                        is ChangeEmailFragment -> {
                            navController.navigate(R.id.action_changeEmailFragment_to_userFragment)
                            navController.clearBackStack("")
                        }
                        is HistoryFragment -> {
                            navController.navigate(R.id.action_historyFragment_to_userFragment)
                            navController.clearBackStack("")
                        }
                        is GameDetailsFragment -> {
                            navController.navigate(R.id.action_global_userFragment)
                            navController.clearBackStack("")
                        }
                    }
                }
            }
        }

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    override fun attachBaseContext(newBase: Context) {
        preferences = newBase.getSharedPreferences(newBase.getString(R.string.setting_preferences), MODE_PRIVATE)
        val language = preferences.getString(newBase.getString(R.string.language_settings), "en")
        val context = ContextWrapper.wrap(newBase, language!!)
        super.attachBaseContext(context)
    }
}

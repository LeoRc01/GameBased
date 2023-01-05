package com.cip.cipstudio.view

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.cip.cipstudio.R
import com.cip.cipstudio.utils.ContextWrapper
import com.cip.cipstudio.view.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private lateinit var navController: NavController
    lateinit var bottomNavigationView : BottomNavigationView
    lateinit var root : View
    lateinit var containerView: FragmentContainerView
    private val TAG = "MainActivity"
    private var bottomMargin = 0
    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val heightDiff = (root.rootView.height - root.height) * 100 / root.rootView.height
        if (heightDiff > 15) { // if more than 100 pixels, its probably a keyboard...
            bottomNavigationView.visibility = View.GONE
            if (bottomMargin == 0)
                bottomMargin = (containerView.layoutParams as MarginLayoutParams).bottomMargin
            (containerView.layoutParams as MarginLayoutParams).bottomMargin = 0
        } else {
            if (bottomMargin == 0)
                bottomMargin = (containerView.layoutParams as MarginLayoutParams).bottomMargin
            bottomNavigationView.visibility = View.VISIBLE
            (containerView.layoutParams as MarginLayoutParams).bottomMargin = bottomMargin

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar!!.hide()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.a_main_cv_container) as NavHostFragment
        navController = navHostFragment.navController

        root = findViewById<View>(R.id.a_main_cl_rootLayout)
        root.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)

        containerView = findViewById<FragmentContainerView>(R.id.a_main_cv_container)

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


        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    val gameId : String = deepLink!!.getQueryParameter("gameId").toString()
                    val bundle = bundleOf()
                    bundle.putString("game_id", gameId)
                    navController.navigate(R.id.action_homeScreen_to_game_details_home, bundle)
                }

                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...
            }
            .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }

    }

    override fun attachBaseContext(newBase: Context) {
        preferences = newBase.getSharedPreferences(newBase.getString(R.string.setting_preferences), MODE_PRIVATE)
        val language = preferences.getString(newBase.getString(R.string.language_settings), "en")
        val context = ContextWrapper.wrap(newBase, language!!)
        super.attachBaseContext(context)
    }
}

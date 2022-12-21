package com.cip.cipstudio.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.cip.cipstudio.exception.NotLoggedException
import com.cip.cipstudio.R
import com.cip.cipstudio.dataSource.repository.historyRepository.HistoryRepositoryLocal
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.ContextWrapper

class AuthActivity : AppCompatActivity() {
    private val TAG = "AuthActivity"

    private lateinit var navController: NavController
    private lateinit var preferences : android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (!preferences.contains(getString(R.string.dark_mode_settings))) {
            preferences.edit().putBoolean(getString(R.string.dark_mode_settings), true).apply()
        }

        if (preferences.getBoolean(getString(R.string.dark_mode_settings), false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }



        setContentView(R.layout.activity_auth)

        if(User.isLogged() || preferences.contains(getString(R.string.guest_settings))) {
            try {

                User.syncRecentlyViewedGames(HistoryRepositoryLocal(this))
                Log.i(TAG, "Login as User")

            } catch (_: NotLoggedException) {
                Log.i(TAG, "Login as guest")
            }
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

    override fun attachBaseContext(newBase: Context) {
        preferences = newBase.getSharedPreferences(newBase.getString(R.string.setting_preferences), MODE_PRIVATE)
        val language = preferences.getString(newBase.getString(R.string.language_settings), "en")
        val context = ContextWrapper.wrap(newBase, language!!)
        super.attachBaseContext(context)
    }
}

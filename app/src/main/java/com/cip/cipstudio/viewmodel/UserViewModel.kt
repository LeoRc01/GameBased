package com.cip.cipstudio.viewmodel

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentUserBinding
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.view.AuthActivity
import com.cip.cipstudio.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.*


class UserViewModel(val binding : FragmentUserBinding) : ViewModel() {
    private val TAG = "UserViewModel"
    private val preferences = binding.root.context.getSharedPreferences(binding.root.context.getString(R.string.setting_preferences), 0)
    private val firebase: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentLanguage: String
    private val systemLanguage: String = Locale.getDefault().language

    fun logout(onSuccess: () -> Unit,
               onFailure: (AuthErrorEnum) -> Unit = {}) {
        firebase.signOut()
        onSuccess.invoke()
    }

    fun setDarkMode(onSuccess: () -> Unit) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        onSuccess.invoke()
    }

    fun setLightMode(onSuccess: () -> Unit) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        onSuccess.invoke()
    }

    fun setItalianLanguage(onSuccess: () -> Unit) {
        setLocale("it")
        onSuccess.invoke()
    }

    private fun setLocale(s: String) {
        Log.d(TAG, "Lingua italiana")
        currentLanguage = preferences.getString("language", systemLanguage).toString()
        if (currentLanguage != s) {
            val locale = Locale(s)
            Locale.setDefault(locale)
            val config = binding.root.context.resources.configuration
            config.setLocale(locale)
            binding.root.context.resources.updateConfiguration(config, binding.root.context.resources.displayMetrics)
            preferences.edit().putString("language", s).apply()
            val refresh = Intent(binding.root.context, MainActivity::class.java)
            binding.root.context.startActivity(refresh)
        } else {
            Log.d(TAG, "Lingua già impostata")
        }

    }

    fun setEnglishLanguage(onSuccess: () -> Unit) {
        setLocale("en")
        onSuccess.invoke()
    }


}
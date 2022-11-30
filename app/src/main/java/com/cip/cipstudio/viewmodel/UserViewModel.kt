package com.cip.cipstudio.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cip.cipstudio.R
import com.cip.cipstudio.dataSource.repository.HistoryRepository
import com.cip.cipstudio.dataSource.repository.historyRepositoryImpl.HistoryRepositoryLocal
import com.cip.cipstudio.databinding.FragmentUserBinding
import com.cip.cipstudio.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*


class UserViewModel(val binding : FragmentUserBinding) : ViewModel() {
    private val TAG = "UserViewModel"
    private val preferences = binding.root.context.getSharedPreferences(binding.root.context.getString(R.string.setting_preferences), 0)
    private val db = HistoryRepositoryLocal(binding.root.context)

    fun logout(onSuccess: () -> Unit, onFailure: () -> Unit = {}) {
        try {
            User.logout()
            onSuccess.invoke()
        }
        catch (e: Exception) {
            Log.e(TAG, "logout: ${e.message}")
            onFailure.invoke()
        }

    }

    fun setDarkMode(onSuccess: () -> Unit) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        onSuccess.invoke()
    }

    fun setLightMode(onSuccess: () -> Unit) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        onSuccess.invoke()
    }

    fun setLanguage(lan: String, onSuccess: () -> Unit) {
        Log.i(TAG, "setLanguage: $lan")
        if (lan == Locale.getDefault().language)
            return
        preferences.edit().putString(binding.root.context.getString(R.string.language_settings), lan).apply()
        onSuccess.invoke()
    }

    fun deleteAccount(onSuccess: () -> Unit, OnFailure: () -> Unit = {}) {
        viewModelScope.launch {
            User.delete(db).addOnSuccessListener {
                onSuccess.invoke()
            }.addOnFailureListener(){
                OnFailure.invoke()
            }
        }
    }


}
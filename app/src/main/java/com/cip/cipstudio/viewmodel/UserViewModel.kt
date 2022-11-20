package com.cip.cipstudio.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import com.cip.cipstudio.databinding.FragmentUserBinding
import com.cip.cipstudio.utils.AuthErrorEnum
import com.google.firebase.auth.FirebaseAuth


class UserViewModel(val binding : FragmentUserBinding) : ViewModel() {
    private val TAG = "UserViewModel"

    private val firebase: FirebaseAuth = FirebaseAuth.getInstance()

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


}
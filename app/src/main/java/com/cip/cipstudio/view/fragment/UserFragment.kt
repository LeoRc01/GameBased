package com.cip.cipstudio.view.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentUserBinding
import com.cip.cipstudio.view.AuthActivity
import com.cip.cipstudio.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth

class UserFragment : Fragment() {
    private val TAG = "UserFragment"


    private lateinit var userViewModel: UserViewModel
    private lateinit var userBinding: FragmentUserBinding

    private lateinit var preferences : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferences = requireContext().getSharedPreferences(getString(R.string.setting_preferences), MODE_PRIVATE)

        userBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        userViewModel = UserViewModel(userBinding)


        val currentUser = FirebaseAuth.getInstance().currentUser
        userBinding.fUserTvUsername.text = currentUser?.email


        userBinding.fUserTvLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
            userViewModel.logout(onSuccess = {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()})
        }

        userBinding.fUserTvChangeEmail.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_changeEmailFragment)
        }


        userBinding.fUserScDarkMode.isChecked = preferences.getBoolean(getString(R.string.dark_mode_settings), false)
        userBinding.fUserScDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userViewModel.setDarkMode(onSuccess = {
                    preferences.edit().putBoolean(getString(R.string.dark_mode_settings), true).apply()
                })
            } else {
                userViewModel.setLightMode(onSuccess = {
                    preferences.edit().putBoolean(getString(R.string.dark_mode_settings), false).apply()
                })
            }
        }



        return userBinding.root
    }
}
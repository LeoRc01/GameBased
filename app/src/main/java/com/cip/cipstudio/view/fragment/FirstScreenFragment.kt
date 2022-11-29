package com.cip.cipstudio.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentFirstScreenBinding
import com.cip.cipstudio.view.MainActivity

class FirstScreenFragment : Fragment() {

    private lateinit var firstScreenBinding: FragmentFirstScreenBinding
    private lateinit var preferences : android.content.SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firstScreenBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_first_screen, container, false)
        preferences = firstScreenBinding.root.context.getSharedPreferences(getString(R.string.setting_preferences),
            AppCompatActivity.MODE_PRIVATE)
        if (preferences.contains(getString(R.string.to_login))) {
            preferences.edit().remove(getString(R.string.to_login)).apply()
            findNavController().navigate(R.id.action_firstScreenFragment_to_loginFragment)
        }

        firstScreenBinding.fFirstScreenBtnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_firstScreenFragment_to_loginFragment)
        }

        firstScreenBinding.fFirstScreenBtnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_firstScreenFragment_to_registerFragment)
        }

        firstScreenBinding.fFirstScreenBtnGuest.setOnClickListener {
            preferences.edit().putBoolean(getString(R.string.guest_settings), true).apply()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            this.activity?.finish()
        }



        return firstScreenBinding.root
    }

}
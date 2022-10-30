package com.cip.cipstudio.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentLoginBinding
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class LoginFragment : Fragment() {

    private lateinit var loginBinding: FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        authViewModel = AuthViewModel(requireContext())
        loginBinding.authViewModel = authViewModel


        loginBinding.fLoginTvSwitchMode.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        initializeLoginButton()
        return loginBinding.root
    }

    private fun initializeLoginButton(){
        loginBinding.fLoginBtnLogin.setOnClickListener {
            loginBinding.fLoginLayoutEmail.error = ""
            loginBinding.fLoginLayoutPwd.error = ""
            authViewModel
                .login(loginBinding.fLoginLayoutEmail, loginBinding.fLoginLayoutPwd) {
                    val i = Intent(this.requireContext(), MainActivity::class.java)
                    startActivity(i)
                }
        }
    }

}
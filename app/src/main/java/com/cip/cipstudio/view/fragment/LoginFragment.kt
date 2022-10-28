package com.cip.cipstudio.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class LoginFragment : Fragment() {

    private lateinit var loginBtn : Button
    private lateinit var authViewModel: AuthViewModel
    private lateinit var emailEt : TextInputEditText
    private lateinit var pwdEt : TextInputEditText
    private lateinit var emailLayout : TextInputLayout
    private lateinit var pwdLayout : TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val changeAuthTextView = view.findViewById<TextView>(R.id.f_login_tv_switchMode)
        authViewModel = AuthViewModel(requireContext())
        loginBtn = view.findViewById(R.id.f_login_btn_login)
        emailEt = view.findViewById(R.id.f_login_et_email)
        emailLayout = view.findViewById(R.id.f_login_layout_email)
        pwdEt = view.findViewById(R.id.f_login_et_pwd)
        pwdLayout = view.findViewById(R.id.f_login_layout_pwd)

        changeAuthTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        initializeLoginButton()
        return view
    }

    private fun initializeLoginButton(){
        loginBtn.setOnClickListener {
            emailLayout.error = null
            pwdLayout.error = null
            authViewModel
                .login( emailEt.text.toString().trim(), pwdEt.text.toString(), emailLayout, pwdLayout) {
                    val i = Intent(this.requireContext(), MainActivity::class.java)
                    startActivity(i)
                }
        }
    }

}
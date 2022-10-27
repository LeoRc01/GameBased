package com.cip.cipstudio

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
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

        val changeAuthTextView = view.findViewById<TextView>(R.id.tvSwitchMode)
        authViewModel = AuthViewModel(requireContext())
        loginBtn = view.findViewById(R.id.btLogin)
        emailEt = view.findViewById(R.id.emailEt)
        emailLayout = view.findViewById(R.id.emailLayout)
        pwdEt = view.findViewById(R.id.pwdEt)
        pwdLayout = view.findViewById(R.id.pwdLayout)

        changeAuthTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        initalizeLoginButton()
        return view
    }

    private fun initalizeLoginButton(){
        loginBtn.setOnClickListener {
            authViewModel
                .login( emailEt.text.toString(), pwdEt.text.toString(), emailLayout, pwdLayout) {
                    val i = Intent(this.requireContext(), MainActivity::class.java)
                    startActivity(i)
                }
        }
    }

}
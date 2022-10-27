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


class RegisterFragment : Fragment() {

    private lateinit var registerBtn : Button
    private lateinit var authViewModel: AuthViewModel
    private lateinit var emailEt : TextInputEditText
    private lateinit var pwdEt : TextInputEditText
    private lateinit var emailLayout : TextInputLayout
    private lateinit var pwdLayout : TextInputLayout
    private lateinit var pwdConfirmEt : TextInputEditText
    private lateinit var pwdConfirmLayout : TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val changeAuthTextView = view.findViewById<TextView>(R.id.tvSwitchMode)
        authViewModel = AuthViewModel(requireContext())
        registerBtn = view.findViewById(R.id.btRegister)
        emailEt = view.findViewById(R.id.emailEt)
        emailLayout = view.findViewById(R.id.emailLayout)
        pwdEt = view.findViewById(R.id.pwdEt)
        pwdLayout = view.findViewById(R.id.pwdLayout)
        pwdConfirmEt = view.findViewById(R.id.pwdrEt)
        pwdConfirmLayout = view.findViewById(R.id.pwdrLayout)

        changeAuthTextView.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        initalizeLoginButton()
        return view
    }

    private fun initalizeLoginButton(){
        registerBtn.setOnClickListener {
            authViewModel
                .register( emailEt.text.toString(), pwdEt.text.toString(),
                    pwdConfirmEt.text.toString(),
                    emailLayout, pwdLayout, pwdConfirmLayout) {
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
        }
    }
}
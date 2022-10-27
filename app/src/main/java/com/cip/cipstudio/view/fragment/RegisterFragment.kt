package com.cip.cipstudio.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
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

        val changeAuthTextView = view.findViewById<TextView>(R.id.f_register_tv_switchMode)
        authViewModel = AuthViewModel(requireContext())
        registerBtn = view.findViewById(R.id.f_register_btn_register)
        emailEt = view.findViewById(R.id.f_register_et_email)
        emailLayout = view.findViewById(R.id.f_register_layout_email)
        pwdEt = view.findViewById(R.id.f_register_et_pwd)
        pwdLayout = view.findViewById(R.id.f_register_layout_pwd)
        pwdConfirmEt = view.findViewById(R.id.f_register_et_pwdConfirm)
        pwdConfirmLayout = view.findViewById(R.id.f_register_layout_pwdConfirm)

        changeAuthTextView.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        initializeRegisterButton()
        return view
    }

    private fun initializeRegisterButton(){
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
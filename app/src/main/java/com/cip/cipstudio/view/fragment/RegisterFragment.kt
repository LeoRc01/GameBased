package com.cip.cipstudio.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentRegisterBinding
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.viewmodel.LoginViewModel
import com.cip.cipstudio.viewmodel.RegisterViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class RegisterFragment : Fragment() {

    private lateinit var registerBinding : FragmentRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        registerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        registerViewModel = RegisterViewModel(requireContext())
        registerBinding.registerViewModel = registerViewModel

        registerBinding.fRegisterTvSwitchMode.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        initializeRegisterButton()
        return registerBinding.root
    }

    private fun initializeRegisterButton(){
        registerBinding.fRegisterBtnRegister.setOnClickListener {
            registerBinding.fRegisterLayoutEmail.error = ""
            registerBinding.fRegisterLayoutPwd.error = ""
            registerBinding.fRegisterLayoutPwdConfirm.error = ""
            registerViewModel
                .register(onSuccess = {
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                },
                    onFailure = {
                        when(it){
                            AuthErrorEnum.EMAIL_NOT_VALID -> {
                                registerBinding.fRegisterLayoutEmail.error = it.getErrorMessage(this.requireContext())
                            }
                            AuthErrorEnum.EMAIL_ALREADY_IN_USE -> {
                                Toast.makeText(context, R.string.email_already_in_use, Toast.LENGTH_LONG).show()
                            }
                            AuthErrorEnum.PASSWORD_NOT_VALID, AuthErrorEnum.PASSWORD_TOO_SHORT, AuthErrorEnum.PASSWORD_TOO_LONG, AuthErrorEnum.PASSWORD_NO_SPECIAL_CHARACTER, AuthErrorEnum.PASSWORD_NO_DIGIT, AuthErrorEnum.PASSWORD_NO_LOWERCASE, AuthErrorEnum.PASSWORD_NO_UPPERCASE -> {
                                registerBinding.fRegisterLayoutPwd.error = it.getErrorMessage(this.requireContext())
                            }
                            AuthErrorEnum.PASSWORDS_NOT_MATCH -> {
                                registerBinding.fRegisterLayoutPwdConfirm.error = it.getErrorMessage(this.requireContext())
                            }
                            AuthErrorEnum.UNKNOWN_ERROR -> {
                                Toast.makeText(context, it.getErrorMessage(this.requireContext()), Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(context, R.string.internal_error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
        }
    }
}
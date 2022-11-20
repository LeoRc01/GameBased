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
import com.cip.cipstudio.utils.AuthTypeErrorEnum
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
    ): View {
        registerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        registerViewModel = RegisterViewModel()
        registerBinding.registerViewModel = registerViewModel
        registerBinding.executePendingBindings()

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
            registerBinding.fRegisterLayoutUsername.error = ""
            registerViewModel
                .register(requireContext(),
                    onSuccess = {
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                },
                    onFailure = {
                        when(it.getErrorType()){
                            AuthTypeErrorEnum.USERNAME -> {
                                registerBinding.fRegisterLayoutUsername.error = getString(it.getErrorId())
                            }
                            AuthTypeErrorEnum.EMAIL -> {
                                registerBinding.fRegisterLayoutEmail.error = getString(it.getErrorId())
                            }
                            AuthTypeErrorEnum.PASSWORD -> {
                                registerBinding.fRegisterLayoutPwd.error = getString(it.getErrorId())
                            }
                            AuthTypeErrorEnum.CONFIRM_PASSWORD -> {
                                registerBinding.fRegisterLayoutPwdConfirm.error = getString(it.getErrorId())
                            }
                            else -> {
                                Toast.makeText(context, getString(it.getErrorId()), Toast.LENGTH_SHORT).show()
                            }

                        }
                    })
        }
    }
}
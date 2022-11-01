package com.cip.cipstudio.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentLoginBinding
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.viewmodel.AuthViewModel


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
        loginBinding.fLoginTvForgotPwd.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_passwordResetFragment)
        }

        initializeLoginButton()
        return loginBinding.root
    }

    private fun initializeLoginButton(){
        loginBinding.fLoginBtnLogin.setOnClickListener {
            loginBinding.fLoginLayoutEmail.error = ""
            loginBinding.fLoginLayoutPwd.error = ""
            authViewModel
                .login(onSuccess = {
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    },
                    onFailure = {
                        when(it){
                            AuthErrorEnum.EMAIL_NOT_VALID, AuthErrorEnum.EMAIL_NOT_REGISTERED -> {
                                loginBinding.fLoginLayoutEmail.error = it.getErrorMessage(this.requireContext())
                            }
                            AuthErrorEnum.PASSWORD_NOT_CORRECT -> {
                                loginBinding.fLoginLayoutPwd.error = it.getErrorMessage(this.requireContext())
                            }
                            AuthErrorEnum.UNKNOWN_ERROR -> {
                                Toast.makeText(context, it.getErrorMessage(this.requireContext()), Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(context, "Internal error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
        }
    }

}
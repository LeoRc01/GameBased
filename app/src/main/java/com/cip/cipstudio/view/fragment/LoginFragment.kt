package com.cip.cipstudio.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentLoginBinding
import com.cip.cipstudio.utils.AuthTypeErrorEnum
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.cip.cipstudio.viewmodel.LoginViewModel


class LoginFragment : Fragment() {

    private lateinit var loginBinding: FragmentLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var preferences : android.content.SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        loginViewModel = LoginViewModel(loginBinding)
        loginBinding.loginViewModel = loginViewModel
        loginBinding.executePendingBindings()

        preferences = loginBinding.root.context.getSharedPreferences(getString(R.string.setting_preferences),
            AppCompatActivity.MODE_PRIVATE)

        if (preferences.contains(getString(R.string.to_login))) {
            preferences.edit().remove(getString(R.string.to_login)).apply()
        }

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
            LoadingSpinner.showLoadingDialog(requireContext())
            loginBinding.fLoginLayoutEmail.error = ""
            loginBinding.fLoginLayoutPwd.error = ""
            loginViewModel
                .login(onSuccess = {
                        LoadingSpinner.dismiss()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    },
                    onFailure = {
                        LoadingSpinner.dismiss()
                        when(it.getErrorType()){
                            AuthTypeErrorEnum.EMAIL -> {
                                loginBinding.fLoginLayoutEmail.error = getString(it.getErrorId())
                            }
                            AuthTypeErrorEnum.PASSWORD -> {
                                loginBinding.fLoginLayoutPwd.error = getString(it.getErrorId())
                            }
                            AuthTypeErrorEnum.UNKNOWN -> {
                                Toast.makeText(context, getString(it.getErrorId()), Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(context, getString(R.string.internal_error), Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
        }
    }

}
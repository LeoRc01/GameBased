package com.cip.cipstudio.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentPasswordResetBinding
import com.cip.cipstudio.viewmodel.LoginViewModel

class PasswordResetFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var resetPasswordResetBinding: FragmentPasswordResetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        resetPasswordResetBinding = FragmentPasswordResetBinding.inflate(inflater, container, false)
        loginViewModel = LoginViewModel(requireContext())

        resetPasswordResetBinding.authViewModel = loginViewModel

        resetPasswordResetBinding.fPasswordResetIconBtnBack.setOnClickListener {
            findNavController().navigate(R.id.action_passwordResetFragment_to_loginFragment)
        }

        return resetPasswordResetBinding.root
    }
}
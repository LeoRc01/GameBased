package com.cip.cipstudio.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentPasswordResetBinding
import com.cip.cipstudio.viewmodel.LoginViewModel
import com.cip.cipstudio.viewmodel.PasswordResetViewModel

class PasswordResetFragment : Fragment() {

    private lateinit var passwordResetViewModel: PasswordResetViewModel
    private lateinit var passwordResetBinding: FragmentPasswordResetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        passwordResetBinding = FragmentPasswordResetBinding.inflate(inflater, container, false)
        passwordResetViewModel = PasswordResetViewModel()

        passwordResetBinding.passwordResetViewModel = passwordResetViewModel

        passwordResetBinding.fPasswordResetIconBtnBack.setOnClickListener {
            findNavController().navigate(R.id.action_passwordResetFragment_to_loginFragment)
        }

        passwordResetBinding.fPasswordResetBtnSendEmail.setOnClickListener {
            passwordResetBinding.fPasswordResetLayoutEmail.error = ""
            passwordResetViewModel.resetPassword(
                onSuccess = {
                    Toast.makeText(requireContext(), "Email sent", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_passwordResetFragment_to_loginFragment)
                },
                onFailure = {
                    passwordResetBinding.fPasswordResetLayoutEmail.error = getString(it.getErrorId())
                }
            )
        }

        return passwordResetBinding.root
    }
}
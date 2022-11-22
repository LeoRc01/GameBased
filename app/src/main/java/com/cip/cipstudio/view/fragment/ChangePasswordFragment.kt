package com.cip.cipstudio.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentPasswordChangeBinding
import com.cip.cipstudio.utils.AuthTypeErrorEnum
import com.cip.cipstudio.view.AuthActivity
import com.cip.cipstudio.viewmodel.ChangePasswordViewModel
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordFragment : Fragment() {
    private val TAG = "ChangePasswordFragment"

    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    private lateinit var changePasswordBinding: FragmentPasswordChangeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        changePasswordBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_password_change, container, false)
        changePasswordViewModel = ChangePasswordViewModel(changePasswordBinding)
        changePasswordBinding.changePasswordViewModel = changePasswordViewModel


        val currentUser = FirebaseAuth.getInstance().currentUser
        changePasswordBinding.fPasswordChangeTvUsername.text = currentUser?.displayName
        changePasswordBinding.fPasswordChangeTvEmail.text = currentUser?.email

        initializeChangePasswordButton()

        return changePasswordBinding.root
    }

    private fun initializeChangePasswordButton() {
        changePasswordBinding.fPasswordChangeBtnChange.setOnClickListener {
            changePasswordViewModel.changePassword(
                onSuccess = {
                    Toast.makeText(requireContext(), "Password changed, please login", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(requireContext(), AuthActivity::class.java))
                    requireActivity().finish()
                },
                onFailure = {
                    when(it.getErrorType()){
                        AuthTypeErrorEnum.EMAIL -> {
                            changePasswordBinding.fPasswordChangeLayoutEmail.error = getString(it.getErrorId())
                        }
                        AuthTypeErrorEnum.PASSWORD -> {
                            changePasswordBinding.fPasswordChangeLayoutNewpwd.error = getString(it.getErrorId())
                        }
                        AuthTypeErrorEnum.CONFIRM_PASSWORD -> {
                            changePasswordBinding.fPasswordChangeLayoutConfirmpwd.error = getString(it.getErrorId())
                        }
                        AuthTypeErrorEnum.UNKNOWN -> {
                            Toast.makeText(context, getString(it.getErrorId()), Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, getString(R.string.internal_error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}
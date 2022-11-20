package com.cip.cipstudio.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentEmailChangeBinding
import com.cip.cipstudio.viewmodel.ChangeEmailViewModel
import com.google.firebase.auth.FirebaseAuth

class ChangeEmailFragment : Fragment() {
    private val TAG = "ChangeEmailFragment"

    private lateinit var changeEmailViewModel: ChangeEmailViewModel
    private lateinit var changeEmailBinding: FragmentEmailChangeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changeEmailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_email_change, container, false)
        changeEmailViewModel = ChangeEmailViewModel(changeEmailBinding)
        changeEmailBinding.changeEmailViewModel = changeEmailViewModel

        val currentUser = FirebaseAuth.getInstance().currentUser
        changeEmailBinding.fEmailChangeTvEmail.text = currentUser?.email


        initializeChangeEmailButton()

        return changeEmailBinding.root
    }

    private fun initializeChangeEmailButton() {
        changeEmailBinding.fEmailChangeBtnChange.setOnClickListener {
            changeEmailViewModel.changeEmail(
                onSuccess = {
                    Toast.makeText(requireContext(), "Email changed", Toast.LENGTH_SHORT).show()
                    findNavController(requireView()).navigate(R.id.action_changeEmailFragment_to_userFragment)
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
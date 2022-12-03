package com.cip.cipstudio.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentUsernameChangeBinding
import com.cip.cipstudio.model.User
import com.cip.cipstudio.utils.AuthTypeErrorEnum
import com.cip.cipstudio.view.AuthActivity
import com.cip.cipstudio.viewmodel.ChangeUsernameViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ChangeUsernameFragment : Fragment() {
    private val TAG = "ChangeUsernameFragment"

    private lateinit var changeUsernameViewModel: ChangeUsernameViewModel
    private lateinit var changeUsernameBinding: FragmentUsernameChangeBinding
    private val user = User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        changeUsernameBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_username_change, container, false)
        changeUsernameViewModel = ChangeUsernameViewModel()
        changeUsernameBinding.changeUsernameViewModel = changeUsernameViewModel
        changeUsernameBinding.executePendingBindings()

        changeUsernameBinding.fUsernameChangeBtnBack.root.setOnClickListener {
            findNavController().popBackStack()
        }

        changeUsernameBinding.fUsernameChangeTvUsername.text = user.username
        changeUsernameBinding.fUsernameChangeTvEmail.text = user.email
        user.downloadUrl.let {
            if (it != null) {
                Log.d(TAG, "Photo download url: $it")
                Picasso.get().load(it).into(changeUsernameBinding.fUsernameChangeIvProfilePicture)
            }
            else {
                Log.d(TAG, "no photo")
            }
        }



        initializeChangeUsernameButton()

        return changeUsernameBinding.root

    }

    private fun initializeChangeUsernameButton() {
        changeUsernameBinding.fUsernameChangeBtnChange.setOnClickListener {
            changeUsernameBinding.fUsernameChangeLayoutNewUsername.error = ""
            changeUsernameViewModel.changeUsername(
                onSuccess = {
                    Log.d(TAG, "Username changed")
                    findNavController().navigate(R.id.action_changeUsernameFragment_to_userFragment)
                },
                onFailure = {
                    when(it.getErrorType()){
                        AuthTypeErrorEnum.USERNAME -> {
                            changeUsernameBinding.fUsernameChangeLayoutNewUsername.error =
                                getString(it.getErrorId())
                        }
                        AuthTypeErrorEnum.UNKNOWN, AuthTypeErrorEnum.LOGIN -> {
                            Toast.makeText(requireContext(), it.getErrorId(), Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            )
        }
    }


}
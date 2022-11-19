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
import com.cip.cipstudio.databinding.FragmentUserBinding
import com.cip.cipstudio.view.AuthActivity
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.viewmodel.UserViewModel

class UserFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var userBinding: FragmentUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        userViewModel = UserViewModel(userBinding)


        userBinding.fUserTvLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
            userViewModel.logout(onSuccess = {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()})
        }

        return userBinding.root
    }
}
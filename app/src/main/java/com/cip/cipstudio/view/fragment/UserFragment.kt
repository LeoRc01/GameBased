package com.cip.cipstudio.view.fragment

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.cip.cipstudio.model.User.photoUrl
import com.cip.cipstudio.model.User.username
import com.cip.cipstudio.view.AuthActivity
import com.cip.cipstudio.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import io.grpc.Context.Storage
import java.util.*

class UserFragment : Fragment() {
    private val TAG = "UserFragment"


    private lateinit var userViewModel: UserViewModel
    private lateinit var userBinding: FragmentUserBinding

    private lateinit var preferences : SharedPreferences
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var selectedPhotoUri : Uri? = null
    private val filename = currentUser?.uid
    private val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferences = requireContext().getSharedPreferences(getString(R.string.setting_preferences), MODE_PRIVATE)

        userBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        userViewModel = UserViewModel(userBinding)



        userBinding.fUserTvUsername.text = currentUser?.displayName
        userBinding.fUserTvEmail.text = currentUser?.email
        ref.downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(userBinding.fUserIwProfilePicture)
        }



        userBinding.fUserTvLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
            userViewModel.logout(onSuccess = {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()})
        }

        userBinding.fUserTvChangeEmail.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_changeEmailFragment)
        }

        userBinding.fUserTvChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_changePasswordFragment)
        }
        
        userBinding.fUserTvChangeUsername.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_changeUsernameFragment)
        }


        userBinding.fUserScDarkMode.isChecked = preferences.getBoolean(getString(R.string.dark_mode_settings), false)
        userBinding.fUserScDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userViewModel.setDarkMode(onSuccess = {
                    preferences.edit().putBoolean(getString(R.string.dark_mode_settings), true).apply()
                })
            } else {
                userViewModel.setLightMode(onSuccess = {
                    preferences.edit().putBoolean(getString(R.string.dark_mode_settings), false).apply()
                })
            }
        }

        userBinding.fUserIwProfilePicture.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


        return userBinding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedPhotoUri)
            userBinding.fUserIwProfilePicture.setImageBitmap(bitmap)

            ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
                    Log.d(TAG, "Success upload: ${it.metadata?.path}")
            }.addOnFailureListener{
                Log.d(TAG, "Failed upload: ${it.message}")
            }


        }
    }
}
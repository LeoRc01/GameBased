package com.cip.cipstudio.view.fragment


import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentUserBinding
import com.cip.cipstudio.model.User
import com.cip.cipstudio.view.AuthActivity
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*


class UserFragment : Fragment() {
    private val TAG = "UserFragment"


    private lateinit var userViewModel: UserViewModel
    private lateinit var userBinding: FragmentUserBinding
    private lateinit var preferences : SharedPreferences
    private val systemLanguage: String = Locale.getDefault().language
    private val user = User

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        preferences = requireContext().getSharedPreferences(getString(com.cip.cipstudio.R.string.setting_preferences), MODE_PRIVATE)

        userBinding = DataBindingUtil.inflate(inflater, com.cip.cipstudio.R.layout.fragment_user, container, false)
        userViewModel = UserViewModel(userBinding)



        userBinding.fUserTvUsername.text = user.username
        userBinding.fUserTvEmail.text = user.email

        user.getImage().addOnSuccessListener {
            Log.d(TAG, "Photo download url: $it")
            Picasso.get().load(it).into(userBinding.fUserIwProfilePicture)
        }



        userBinding.fUserTvLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
            userViewModel.logout {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
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

        if (preferences.getString(getString(R.string.language_settings), systemLanguage) == "it") {
            userBinding.fUserActvChangeLanguage.setText(getString(R.string.Italian), false)
        } else {
            userBinding.fUserActvChangeLanguage.setText(getString(R.string.English), false)
        }

        userBinding.fUserActvChangeLanguage.setOnItemClickListener { adapterView, view, i, l ->
            val language = adapterView.getItemAtPosition(i).toString()
            val lan = if (language == getString(R.string.Italian)) {
                "it"
            } else {
                "en"
            }
            userViewModel.setLanguage(lan) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        return userBinding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d(TAG, "Photo was selected")


            val selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedPhotoUri)
            userBinding.fUserIwProfilePicture.setImageBitmap(bitmap)
            user.uploadImage(selectedPhotoUri)

        }
    }
}
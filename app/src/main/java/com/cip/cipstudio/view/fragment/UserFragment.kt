package com.cip.cipstudio.view.fragment


import android.app.Activity
import android.app.AlertDialog
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
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentUserBinding
import com.cip.cipstudio.model.User
import com.cip.cipstudio.view.AuthActivity
import com.cip.cipstudio.view.MainActivity
import com.cip.cipstudio.viewmodel.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        preferences = requireContext().getSharedPreferences(getString(R.string.setting_preferences), MODE_PRIVATE)

        userBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        userViewModel = UserViewModel(userBinding)

        userBinding.userLoggedIn = user.isLogged()


        userBinding.fUserTvUsername.text = user.username
        userBinding.fUserTvEmail.text = user.email

        user.downloadUrl.let {
            if (it != null) {
                Log.d(TAG, "Photo download url: $it")
                Picasso.get().load(it).into(userBinding.fUserIwProfilePicture)
            }
            else {
                Log.d(TAG, "no photo")
            }
        }



        userBinding.fUserTvLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    userViewModel.logout ( onSuccess = {
                        Toast.makeText(requireContext(), "Logout", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), AuthActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    },
                        onFailure = {
                            Toast.makeText(requireContext(), getString(R.string.invalid_operation_must_logged), Toast.LENGTH_SHORT).show()
                        })
                }
                .setNegativeButton(getString(R.string.no)) { _, _ -> }
                .show()
        }

        userBinding.fUserTvChangeEmail.setOnClickListener {
            if (user.isLogged()) {
                findNavController().navigate(R.id.action_userFragment_to_changeEmailFragment)
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.invalid_operation_must_logged), Toast.LENGTH_SHORT).show()
            }
        }

        userBinding.fUserTvChangePassword.setOnClickListener {
            if (user.isLogged()) {
                findNavController().navigate(R.id.action_userFragment_to_changePasswordFragment)
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.invalid_operation_must_logged), Toast.LENGTH_SHORT).show()
            }
        }
        
        userBinding.fUserTvChangeUsername.setOnClickListener {
            if (user.isLogged()) {
                findNavController().navigate(R.id.action_userFragment_to_changeUsernameFragment)
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.invalid_operation_must_logged), Toast.LENGTH_SHORT).show()
            }
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
            if (user.isLogged()) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
            else {
                Toast.makeText(requireContext(), getString(R.string.invalid_operation_must_logged), Toast.LENGTH_SHORT).show()
            }

        }

        if (preferences.getString(getString(R.string.language_settings), systemLanguage) == "it") {
            userBinding.fUserActvChangeLanguage.setText(getString(R.string.Italian), false)
        } else {
            userBinding.fUserActvChangeLanguage.setText(getString(R.string.English), false)
        }

        userBinding.fUserActvChangeLanguage.setOnItemClickListener { adapterView, _, i, _ ->
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

        userBinding.fUserBtnLogin.setOnClickListener {
            preferences.edit().remove(getString(R.string.guest_settings)).apply()
            preferences.edit().putBoolean(getString(R.string.to_login), true).apply()
            val intent = Intent(activity, AuthActivity::class.java)
            startActivity(intent)
        }

        userBinding.fUserTvHistory.setOnClickListener {
             findNavController().navigate(R.id.action_userFragment_to_historyFragment) }

        userBinding.fUserTvDeleteAccount.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_account))
                .setMessage(getString(R.string.delete_account_message))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    userViewModel.deleteAccount(onSuccess = {
                        Toast.makeText(requireContext(), getString(R.string.account_deleted), Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), AuthActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    })
                         {
                            Toast.makeText(requireContext(), getString(R.string.invalid_operation_must_logged), Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton(getString(R.string.no)) { _, _ -> }
                .show()
        }


        userBinding.fUserTvDeleteSearchHistory.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_search_history))
                .setMessage(getString(R.string.delete_search_history_message))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    userViewModel.deleteHistory()
                    Toast.makeText(requireContext(), getString(R.string.search_history_deleted), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.no)) { _, _ -> }
                .show()
        }

        userViewModel.isSearchHistoryEmpty(User.uid) {
            userBinding.fUserTvDeleteSearchHistory.visibility = View.GONE
            userBinding.fUserDividerHistorySearchHistory.visibility = View.GONE
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
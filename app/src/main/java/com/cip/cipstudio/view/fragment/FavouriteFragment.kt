package com.cip.cipstudio.view.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.FavouriteGridViewAdapter
import com.cip.cipstudio.databinding.FragmentFavouriteBinding
import com.cip.cipstudio.view.AuthActivity
import com.cip.cipstudio.viewmodel.FavouriteViewModel

class FavouriteFragment : Fragment() {
    private val TAG = "FavouriteFragment"

    private lateinit var favouriteBinding: FragmentFavouriteBinding
    private lateinit var favouriteViewModel: FavouriteViewModel

    private lateinit var preferences : android.content.SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favouriteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false)

        favouriteViewModel = FavouriteViewModel(favouriteBinding)
        preferences = favouriteBinding.root.context.getSharedPreferences(getString(R.string.setting_preferences),
            AppCompatActivity.MODE_PRIVATE)

        initializeFavourites()

        favouriteBinding.vm = favouriteViewModel
        favouriteBinding.lifecycleOwner = this

        favouriteBinding.fFavouriteSrlRefresh.setOnRefreshListener {
            Log.i(TAG, "Refreshing")
            initializeFavourites(true)
            Handler(Looper.getMainLooper())
                .postDelayed( {
                    favouriteBinding.fFavouriteSrlRefresh.isRefreshing = false
                }, 2000)
        }

        return favouriteBinding.root
    }

    private fun initializeFavourites(refresh: Boolean = false) {
        favouriteViewModel.initialize (refresh,
            updateUI = {
            val gvAdapter = FavouriteGridViewAdapter(requireContext(),
                it,
                favouriteBinding.root.findNavController())
                favouriteBinding.gvFavoriteGames.adapter = gvAdapter
            },
            noFavouriteUI = {
                favouriteBinding.fFragmentTvNoFavourite.visibility = View.VISIBLE
            },
            notLoggedInUI = {
                favouriteBinding.fFavouriteRlFavourites.visibility = View.GONE
                favouriteBinding.fFavouriteLlGuest.visibility = View.VISIBLE
                favouriteBinding.fFavouriteBtnLogin.setOnClickListener {
                    preferences.edit().remove(getString(R.string.guest_settings)).apply()
                    val intent = Intent(activity, AuthActivity::class.java)
                    startActivity(intent)
                }
            })


    }
}
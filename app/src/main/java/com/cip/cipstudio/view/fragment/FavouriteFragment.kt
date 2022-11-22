package com.cip.cipstudio.view.fragment

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.FavouriteGridViewAdapter
import com.cip.cipstudio.databinding.FragmentFavouriteBinding
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.MyFirebaseRepository
import com.cip.cipstudio.viewmodel.FavouriteViewModel
import com.cip.cipstudio.viewmodel.GameDetailsViewModel

class FavouriteFragment : Fragment() {
    private val TAG = "FavouriteFragment"

    private lateinit var favouriteBinding: FragmentFavouriteBinding
    private lateinit var favouriteViewModel: FavouriteViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favouriteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false)

        favouriteViewModel = FavouriteViewModel(favouriteBinding)

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
        favouriteViewModel.initialize (refresh) {
            val gvAdapter = FavouriteGridViewAdapter(requireContext(),
                it,
                R.id.action_fav_to_gameDetailsFragment3,
                favouriteBinding.root.findNavController())
            favouriteBinding.gvFavoriteGames.adapter = gvAdapter
        }

    }
}
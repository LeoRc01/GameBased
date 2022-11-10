package com.cip.cipstudio.view.fragment

import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.cip.cipstudio.R
import com.cip.cipstudio.databinding.FragmentFavouriteBinding
import com.cip.cipstudio.databinding.FragmentGameDetailsBinding
import com.cip.cipstudio.repository.MyFirebaseRepository
import com.cip.cipstudio.viewmodel.FavouriteViewModel
import com.cip.cipstudio.viewmodel.GameDetailsViewModel

class FavouriteFragment : Fragment() {

    private lateinit var favouriteBinding: FragmentFavouriteBinding
    private lateinit var favouriteViewModel: FavouriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        favouriteBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false)

        favouriteViewModel = FavouriteViewModel(favouriteBinding)
        favouriteBinding.vm = favouriteViewModel
        favouriteBinding.lifecycleOwner = this

        return favouriteBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        favouriteBinding.unbind()
    }
}
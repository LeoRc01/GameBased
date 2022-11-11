package com.cip.cipstudio.viewmodel

import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.cip.cipstudio.R
import com.cip.cipstudio.adapters.FavouriteGridViewAdapter
import com.cip.cipstudio.databinding.FragmentFavouriteBinding
import com.cip.cipstudio.model.data.GameDetails
import com.cip.cipstudio.repository.IGDBRepository
import com.cip.cipstudio.repository.IGDBRepositoryRemote
import com.cip.cipstudio.repository.MyFirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FavouriteViewModel(val binding : FragmentFavouriteBinding) : ViewModel() {

    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

    val favouriteGamesIds : ArrayList<String> by lazy {
        arrayListOf()
    }

    lateinit var favouriteGames : ArrayList<GameDetails>
    private lateinit var gvAdapter: BaseAdapter

    init {
        MyFirebaseRepository.getInstance().getFavorites().addOnSuccessListener {
            (it.value as Map<String, Object>).forEach {
                favouriteGamesIds.add(it.value.toString())
            }
            viewModelScope.launch(Dispatchers.Main) {
                val job = viewModelScope.launch(Dispatchers.IO){
                    favouriteGames = IGDBRepositoryRemote.getGamesByIds(favouriteGamesIds) as ArrayList<GameDetails>
                }
                job.join()
                initializeRecyclerView(favouriteGames)
                isPageLoading.postValue(false)
            }
        }
    }

    fun initializeRecyclerView(games : ArrayList<GameDetails>){
        gvAdapter = FavouriteGridViewAdapter(binding.root.context,
            games,
            R.id.action_fav_to_gameDetailsFragment3,
            binding.root.findNavController())
        binding.gvFavoriteGames.adapter = gvAdapter
    }

}
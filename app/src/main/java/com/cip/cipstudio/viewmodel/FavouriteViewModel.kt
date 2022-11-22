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
import kotlinx.coroutines.*

class FavouriteViewModel(val binding : FragmentFavouriteBinding) : ViewModel() {

    val isPageLoading : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

    private val favouriteGamesIds : ArrayList<String> by lazy {
        arrayListOf()
    }

    lateinit var favouriteGames : ArrayList<GameDetails>

    fun initialize(updateUI: (ArrayList<GameDetails>) -> Unit) {
        isPageLoading.postValue(true)
        MyFirebaseRepository.getInstance().getFavorites().addOnSuccessListener {
            (it.value as Map<*, *>).forEach {
                favouriteGamesIds.add(it.value.toString())
            }
            viewModelScope.launch(Dispatchers.Main) {
                favouriteGames = withContext(Dispatchers.IO){
                    IGDBRepositoryRemote.getGamesByIds(favouriteGamesIds) as ArrayList<GameDetails>
                }
                updateUI.invoke(favouriteGames)
                isPageLoading.postValue(false)
            }
        }
    }

}